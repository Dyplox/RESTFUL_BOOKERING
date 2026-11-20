#!/usr/bin/env python3
"""Resume los resultados JUnit de Gradle en Markdown (para $GITHUB_STEP_SUMMARY).

Uso:
  resumen_pruebas.py <directorio-resultados> <título>
  resumen_pruebas.py <directorio-resultados> <título> --vigilar-defectos

Con --vigilar-defectos el directorio debe contener solo la ejecución de @defectoConocido: si algún
escenario PASA, la API corrigió un defecto y el escenario debe dejar de ser @defectoConocido. En ese
caso el script lo lista y termina con código 1. Los escenarios excluidos por tags (skipped) no cuentan.
"""
import sys
import xml.etree.ElementTree as ET
from pathlib import Path


def estado_de(caso):
    if caso.find("skipped") is not None:
        return "excluido", ""
    fallo = caso.find("failure")
    if fallo is None:
        fallo = caso.find("error")
    if fallo is None:
        return "pasa", ""
    mensaje = (fallo.get("message") or "").strip().splitlines()
    return "falla", mensaje[0][:200] if mensaje else ""


def leer_casos(directorio: Path):
    casos = []
    for archivo in sorted(directorio.glob("TEST-*.xml")):
        for caso in ET.parse(archivo).getroot().iter("testcase"):
            estado, mensaje = estado_de(caso)
            casos.append({"nombre": caso.get("name", "?"), "estado": estado, "mensaje": mensaje,
                          "tiempo": float(caso.get("time") or 0)})
    return casos


def tabla(casos, pasan, fallan, excluidos):
    if not casos:
        return ["⚠️ No se encontraron resultados de pruebas."]
    return ["| ✅ Pasan | ❌ Fallan | ⏭️ Excluidos | ⏱️ Tiempo |", "|---|---|---|---|",
            f"| {len(pasan)} | {len(fallan)} | {len(excluidos)} | {sum(c['tiempo'] for c in casos):.1f} s |", ""]


def seccion_vigilancia(pasan, fallan):
    if not pasan:
        return [f"Los {len(fallan)} defectos conocidos siguen reproduciéndose."]
    return (["🎉 **Estos defectos conocidos ya no se reproducen**: la API los corrigió. "
             "Quitar el tag `@defectoConocido` de estos escenarios:", ""]
            + [f"- {c['nombre']}" for c in pasan])


def seccion_fallos(fallan):
    if not fallan:
        return []
    return (["<details><summary>Escenarios que fallan</summary>", ""]
            + [f"- **{c['nombre']}** — {c['mensaje']}" for c in fallan]
            + ["", "</details>"])


def main(argv):
    if len(argv) < 3:
        print(__doc__, file=sys.stderr)
        return 2
    directorio, titulo = Path(argv[1]), argv[2]
    vigilar = "--vigilar-defectos" in argv[3:]
    casos = leer_casos(directorio)
    pasan = [c for c in casos if c["estado"] == "pasa"]
    fallan = [c for c in casos if c["estado"] == "falla"]
    excluidos = [c for c in casos if c["estado"] == "excluido"]

    lineas = [f"### {titulo}", ""] + tabla(casos, pasan, fallan, excluidos)
    lineas += seccion_vigilancia(pasan, fallan) if vigilar else seccion_fallos(fallan)
    print("\n".join(lineas))
    return 1 if vigilar and pasan else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))

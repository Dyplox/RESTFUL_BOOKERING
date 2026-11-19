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


def leer_casos(directorio: Path):
    casos = []
    for archivo in sorted(directorio.glob("TEST-*.xml")):
        raiz = ET.parse(archivo).getroot()
        for caso in raiz.iter("testcase"):
            if caso.find("skipped") is not None:
                estado = "excluido"
            elif caso.find("failure") is not None or caso.find("error") is not None:
                estado = "falla"
            else:
                estado = "pasa"
            fallo = caso.find("failure") if caso.find("failure") is not None else caso.find("error")
            mensaje = (fallo.get("message") or "").strip().splitlines()[0][:200] if fallo is not None else ""
            casos.append({"nombre": caso.get("name", "?"), "estado": estado, "mensaje": mensaje,
                          "tiempo": float(caso.get("time") or 0)})
    return casos


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

    lineas = [f"### {titulo}", ""]
    if not casos:
        lineas.append("⚠️ No se encontraron resultados de pruebas.")
    else:
        lineas += ["| ✅ Pasan | ❌ Fallan | ⏭️ Excluidos | ⏱️ Tiempo |", "|---|---|---|---|",
                   f"| {len(pasan)} | {len(fallan)} | {len(excluidos)} | {sum(c['tiempo'] for c in casos):.1f} s |", ""]
    if vigilar:
        if pasan:
            lineas += ["🎉 **Estos defectos conocidos ya no se reproducen**: la API los corrigió. "
                       "Quitar el tag `@defectoConocido` de estos escenarios:", ""]
            lineas += [f"- {c['nombre']}" for c in pasan]
        else:
            lineas.append(f"Los {len(fallan)} defectos conocidos siguen reproduciéndose.")
    elif fallan:
        lineas += ["<details><summary>Escenarios que fallan</summary>", ""]
        lineas += [f"- **{c['nombre']}** — {c['mensaje']}" for c in fallan]
        lineas += ["", "</details>"]
    print("\n".join(lineas))
    return 1 if vigilar and pasan else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))

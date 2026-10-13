# University Guide

Mod de guía turística para Minecraft Java 1.21.1 y NeoForge.

El NPC guía camina mediante pathfinding hacia paradas invisibles configurables y presenta información al grupo cuando llega.

## Desarrollo

Requiere Java 21. En Windows:

```powershell
.\validate_assets.ps1
.\gradlew.bat build
.\gradlew.bat runGameTestServer
.\gradlew.bat runClient
```

El validador comprueba JSON, referencias de modelos y paridad entre las traducciones en inglés y español.
Los GameTests verifican la serialización de paradas y las altas, cambios y bajas del índice por dimensión.

El JAR compilado se genera en `build/libs/`.

La interfaz incluye un tema verde compartido, contadores de caracteres y paginación
adaptada a la altura de la ventana. Los borradores del editor se conservan al redimensionar.
La revisión visual de las tres pantallas y las pruebas multijugador siguen pendientes.

Este proyecto se publica en `mods/university-guide/` del monorepo
[`uvg-minecraft-mods`](https://github.com/jacobjuarezguerra/uvg-minecraft-mods).
Los registros históricos de pruebas locales están en `REGISTRO_PRUEBAS.md`;
los JAR y mundos mencionados en esos registros no forman parte del repositorio.

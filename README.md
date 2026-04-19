# Snake Swing NetBeans

Ejemplo básico de juego de culebrita (Snake) en Java Swing listo para ejecutar en NetBeans.

## Estructura del proyecto

- `src/` contiene el código fuente Java.
- Proyecto pensado como **Java Application** (sin Maven) para abrir directamente desde NetBeans (`File > Open Project`).

## Cómo ejecutar en NetBeans

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/docenlineacolombia-creator/snake-swing-netbeans.git
   ```
2. Abrir NetBeans y seleccionar **File > Open Project**.
3. Navegar hasta la carpeta del repo y abrir el proyecto.
4. Ejecutar con **Run > Run Project**.

## Lógica del juego

- La culebrita está representada como una lista de segmentos en una grilla.
- Se mueve usando las teclas de dirección.
- Cuando la cabeza toca la comida, crece en 1 segmento y se genera nueva comida.
- El juego detecta colisión contra los bordes y contra el propio cuerpo.

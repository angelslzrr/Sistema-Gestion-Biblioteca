\# Tech Stack - Sistema de Gestión de Biblioteca



\## Entorno de Desarrollo

\- \*\*IDE:\*\* Apache NetBeans IDE 25

\- \*\*Gestor de Construcción:\*\* Apache Ant

\- \*\*Lenguaje:\*\* Java (JDK 21 / JDK 24)



\## Base de Datos e Infraestructura

\- \*\*Motor BD:\*\* Oracle Database (Alojado en contenedor Docker, servicio FREEPDB1)

\- \*\*Cliente de Administración:\*\* Oracle SQL Developer

\- \*\*Control de Versiones:\*\* Git / GitHub

\- \*\*Utilizaremos el driver ojdbc17.jar, sume que la librería se vincula directamente desde las propiedades del IDE en la sección "Libraries". No crear pom.xml.

\- \*\*Restricción de equipo:\* El archivo debe estar ubicado físicamente en la subcarpeta relativa `/lib` dentro del proyecto en NetBeans para garantizar la portabilidad entre las computadoras de los desarrolladores.



\## Librerías y Herramientas Complementarias

\- \*\*Interfaz Gráfica:\*\* Java Swing (nativo en NetBeans)

\- \*\*Gestión de Recursos visuales:\*\* Carpeta `src/recursos/` para logos y fondos.

\- \*\*Integración IA:\*\* Model Context Protocol (MCP) vía Antigravity




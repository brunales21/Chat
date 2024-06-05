# Proyecto de Chat en Java

## Descripción

Este proyecto consiste en un sistema de chat simple desarrollado en Java, utilizando Maven como gestor de dependencias. El sistema de chat permite la creación de canales privados con amigos y es accesible a través de comandos utilizando Telnet. El proyecto está dividido en dos partes: el cliente y el servidor.

- **Servidor**: Alojado en un servidor cloud.
- **Cliente**: Una interfaz gráfica de usuario (GUI) desarrollada con JavaFX.

## Características

- **Creación de Canales**: Los usuarios pueden crear canales para conversaciones grupales seguras y organizadas.
- **Accesibilidad Multiplataforma**: Los usuarios pueden acceder al chat mediante una interfaz gráfica intuitiva o a través de Telnet, lo que permite la conexión desde cualquier dispositivo con capacidades de terminal.
- **Mensajería en Tiempo Real**: Comunicación fluida y en tiempo real entre los participantes del chat.
- **Notificaciones de Mensajes**: Alertas en la interfaz gráfica para nuevos mensajes, asegurando que los usuarios no se pierdan ninguna actualización.
- **Gestión de Usuarios**: Posibilidad de listar todos los usuarios conectados y gestionar las conexiones.
- **Historial de Chat**: Almacenamiento de mensajes para consultar el historial de conversaciones.
- **Seguridad y Privacidad**: Canales seguros que garantizan la privacidad de las conversaciones.
- **Escalabilidad**: Diseño del servidor que permite manejar múltiples conexiones simultáneas sin degradación del rendimiento.
- **Extensible y Personalizable**: Estructura del código que permite añadir nuevas funcionalidades y personalizar la aplicación según las necesidades específicas.
- **Estructura de Proyecto Maven**: Facilita la gestión de dependencias y la construcción del proyecto.

## Requisitos

- **Java 11** o superior
- **Apache Maven 3.6.3** o superior
- **JavaFX** (ya incluido en Java 11 o superior)

## Instalación

### Clonar el repositorio

```sh
git clone https://github.com/tu-usuario/tu-repo.git
cd tu-repo
```
## Uso desde CLI

### Conectarse al servidor

```sh
telnet brunales.com 8080
```

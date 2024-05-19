## How to Run the Project
In order to run the project, follow these steps:
1. Clone the repository to your local machine.
2. Navigate to the project directory in your shell of choice
3. Run the server by running the command:
```console
./gradlew bootRun
```
4. Run the client by running the command:
```console
./gradlew run
```
## Accessing Features
### Admin Interface:
You can access the admin interface by navigating to the admin login through the UI. Then, use the password printed in the console at startup of the server.
### Key Implementations
- **Long-Polling**: We have implemented long-polling in our application. You can find the implementation in [this file](/server/src/main/java/server/implementations/EventServiceImplementation.java) (or here: ‘/server/src/main/java/server/implementations/EventServiceImplementation.java’).
- **Web sockets**: We have implemented web sockets in our application. You can find the implementation in [this file](/server/src/main/java/server/WebSocketConfig.java) (or here: ‘/server/src/main/java/server/WebSocketConfig.java’).
## Changing parameters for the run
- **emailing service**: Please refer to [this file](/server/src/main/resources/application.properties) (or ‘/server/src/main/resources/application.properties’), where you are able to change the emailing service parameters. By default, they are configured to the email of our team. If no configuration is provided, the buttons for sending emails will be grayed out.
- **server URL**: Please refer to [this file](/server/src/main/resources/application.properties) (or ‘/server/src/main/resources/application.properties’), where you are able to change the host and port of the server. By default, they are configured to ‘localhost:8080’.
- **client server URL**: Please refer to [this file](/client/src/main/resources/client.properties) (or ‘/client/src/main/resources/client.properties’), where you are able to change the host and port of the server the client is connected to. By default, they are configured to ‘localhost:8080’. Furthermore, you can also change the server through the UI, with the ‘Change Server’ button in the starter page.
## Extensions and HCI Features
### Keyboard Shortcuts:
We have implemented various keyboard shortcuts to improve the user experience. Some of them are:
- While in the admin overview:
    - Ctrl+H - open the help menu
    - Ctrl+L - change language
    - Ctrl+D - download event information
    - Ctrl+T - import from text
    - Ctrl+I - import from file

Please refer to the shortcut page inside the admin overview for a more complete list of shortcuts.
### Other features:
- **language switch**: Whenever the language is switched, it is also saved in [this file](/client/src/main/resources/client.properties) (or ‘/client/src/main/resources/client.properties’). To add a new language, use the drop-down menu in the starter page, which would create a zip file kit to add a new language. Refer to the README.md file inside the provided kit for a full step-by-step tutorial on adding the language.

## Contact
If you have any questions or need further clarification, feel free to reach out to us. We appreciate your feedback and suggestions to improve our project. We also have the team email:
group04.oopp@gmail.com






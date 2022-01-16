# marscli
>CLI API for getting Mars rovers photos

In order to build the project you need Maven (tested with 3.6.1) and JDK11 or higher installed.
## Build
```sh
mvn clean package
```
## Run
```sh
java -jar <JAR_NAME> <command>
```

## Commands

### Help
Lists general help info
```sh
help
```
Lists help info for specific command
```sh
help <command_name>
```

### Photos
Lists photos taken by Mars rover
```sh
photos --date <yyyy-mm-dd> --days <int> --rover <rovername> --camera <cameraname> --apikey <API_KEY>
```
Any of the options may be omitted in which case the default value will be assumed

Example:  
***java -jar marscli.jar photos -d 2022-01-05 -n 5 -r curiosity -c navcam***  
Gets photos from the Curiosity rover, taken with NAVCAM camera from 2022-01-01 to 2022-01-05
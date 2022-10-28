Ein Chatprogramm bestehend aus 2 Teilen

Server und Client

Der Server und Client haben jeweils eine eigene Main Klasse und müssen getrennt gestartet werden. 
Der Server muss gestartet sein wenn die Clients gestartet werden. Sonst führt dies zum Abbrich des Programms. 

Nachdem der Server läuft muss man sich registrieren. 
Mit "REGISTER:test:password" wird ein benutzer mit dem Namen test und dem passwort password angelegt. 

Sprechen kann man mit:
"SAY:test: Meine Nachricht"

Es wird nun eine Nachricht an den User Test geschireben mit dem Inhalt Meine Nachricht. 

Es können mehrere Clients paralell gestartet werden,

Dazu kann in IntelliJ Edit Run Configuration bei Modify Options "Allow multiple instances" aktiviert werden.

Getestet mit dem OpenJDK-19 Oracle (Via IntelliJ geladen)

# CHAT
Realizzare una SocketServer che gestisce un sistema simile ad una chat, in particolare:
- il server deve gestire più connessioni di socket Client
- ogni Client può inviare dei comandi che devono essere interpretati ed eseguiti lato server

## COMANDI
I comandi disponibili sono:
- \#name <nome utente>
- \#send <nome utente> <messaggio>

Anche il client deve interpretare i messaggi ricevuti dal server
- \#ok (l’operazione del client è andata a buon fine)
 - \#error <messaggio di errore>

## INPUT
La socket client deve ricevere in input i comandi dall’utente attraverso il terminale:
- alla connessione deve richiedere un nome utente (deve ripetere la richiesta fino a quando il nome utente non è valido)
- deve ricevere i comandi dell’utente che sono nella forma <nome utente> <messaggio> (l’utente vuole inviare un messaggio ad un altro utente)
- se l’utente scrive #exit vuol dire che vuole chiudere la connessione

La socket server deve ricevere i comandi dai client ed agire di conseguenza, ovvero inviare il messaggio al client corretto

Gestire i seguenti errori:
- nome utente già esistente
- nome utente a cui inviare il messaggio non esistente

### ATTENZIONE
sarà necessaria una struttura dati condivisa tra i vari Thread della SocketServer per contenere le informazioni sulle socket client connesse.
Gestire questa struttura in modo sincronizzato per evitare problemi di concorrenza
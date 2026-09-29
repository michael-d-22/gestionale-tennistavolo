const sceltaSessione = document.getElementById('scelta-sessione');
const sceltaAtleta = document.getElementById('scelta-atleta');
const formPrenotazione = document.getElementById('form-prenotazione');
const tabellaPrenotazioni = document.getElementById('tabella-prenotazioni');

// La PrenotazioneResponse contiene solo atletaId:
// per mostrare il nome tengo gli atleti in un oggetto { id: "Cognome Nome" }
const nomiAtleti = {};

async function caricaAtletiESessioni() {
    try {
        const atleti = await chiamaApi('GET', '/api/atleti');
        for (const atleta of atleti) {
            nomiAtleti[atleta.id] = atleta.cognome + ' ' + atleta.nome;
            aggiungiOpzione(sceltaAtleta, atleta.id, atleta.cognome + ' ' + atleta.nome + ' (' + atleta.categoria + ')');
        }

        const sessioni = await chiamaApi('GET', '/api/sessioni');
        for (const sessione of sessioni) {
            aggiungiOpzione(sceltaSessione, sessione.id, descriviSessione(sessione));
        }

        if (sessioni.length > 0) {
            caricaPrenotazioni();
        }
    } catch (errore) {
        mostraMessaggio(errore.message, true);
    }
}

async function caricaPrenotazioni() {
    const sessioneId = sceltaSessione.value;
    try {
        const prenotazioni = await chiamaApi('GET', '/api/sessioni/' + sessioneId + '/prenotazioni');
        tabellaPrenotazioni.innerHTML = '';
        for (const prenotazione of prenotazioni) {
            const riga = document.createElement('tr');
            aggiungiCella(riga, prenotazione.id);
            aggiungiCella(riga, nomiAtleti[prenotazione.atletaId]);
            aggiungiCella(riga, prenotazione.stato);

            const azioni = aggiungiCella(riga, '');
            azioni.appendChild(creaPulsante('Presente', function () {
                eseguiAzione(prenotazione.id, 'presenza');
            }));
            azioni.appendChild(creaPulsante('Assente', function () {
                eseguiAzione(prenotazione.id, 'assenza');
            }));
            azioni.appendChild(creaPulsante('Annulla', function () {
                eseguiAzione(prenotazione.id, 'annullamento');
            }));

            tabellaPrenotazioni.appendChild(riga);
        }
    } catch (errore) {
        mostraMessaggio(errore.message, true);
    }
}

// azione = "presenza", "assenza" o "annullamento": sono gli endpoint del PrenotazioneController
async function eseguiAzione(prenotazioneId, azione) {
    try {
        const aggiornata = await chiamaApi('POST', '/api/prenotazioni/' + prenotazioneId + '/' + azione);
        mostraMessaggio('Prenotazione ' + aggiornata.id + ': ' + aggiornata.stato, false);
        caricaPrenotazioni();
    } catch (errore) {
        mostraMessaggio(errore.message, true);
    }
}

formPrenotazione.addEventListener('submit', async function (evento) {
    evento.preventDefault();

    const richiesta = {
        atletaId: Number(sceltaAtleta.value),
        sessioneId: Number(sceltaSessione.value)
    };

    try {
        const creata = await chiamaApi('POST', '/api/prenotazioni', richiesta);
        mostraMessaggio('Prenotato ' + nomiAtleti[creata.atletaId] + ' (' + creata.stato + ')', false);
        caricaPrenotazioni();
    } catch (errore) {
        mostraMessaggio(errore.message, true);
    }
});

sceltaSessione.addEventListener('change', caricaPrenotazioni);

caricaAtletiESessioni();

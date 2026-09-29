const formSessione = document.getElementById('form-sessione');
const tabellaSessioni = document.getElementById('tabella-sessioni');

// La SessioneResponse contiene solo allenatoreId:
// per mostrare il nome tengo gli allenatori in un oggetto { id: "Cognome Nome" }
const nomiAllenatori = {};

async function caricaAllenatori() {
    try {
        const allenatori = await chiamaApi('GET', '/api/allenatori');
        for (const allenatore of allenatori) {
            const nome = allenatore.cognome + ' ' + allenatore.nome;
            nomiAllenatori[allenatore.id] = nome;
            aggiungiOpzione(formSessione.allenatore, allenatore.id, nome);
        }
    } catch (errore) {
        mostraMessaggio(errore.message, true);
    }
}

async function caricaSessioni() {
    try {
        const sessioni = await chiamaApi('GET', '/api/sessioni');
        tabellaSessioni.innerHTML = '';
        for (const sessione of sessioni) {
            const riga = document.createElement('tr');
            aggiungiCella(riga, sessione.id);
            aggiungiCella(riga, sessione.data);
            aggiungiCella(riga, sessione.ora.substring(0, 5));
            aggiungiCella(riga, sessione.tipo);
            aggiungiCella(riga, sessione.categoria);
            aggiungiCella(riga, sessione.capienza);
            aggiungiCella(riga, nomiAllenatori[sessione.allenatoreId]);
            tabellaSessioni.appendChild(riga);
        }
    } catch (errore) {
        mostraMessaggio(errore.message, true);
    }
}

formSessione.addEventListener('submit', async function (evento) {
    evento.preventDefault();

    // I campi lasciati vuoti diventano null, come si aspetta il SessioneRequest
    const categoria = formSessione.categoria.value;
    const capienza = formSessione.capienza.value;
    const allenatore = formSessione.allenatore.value;

    const nuovaSessione = {
        data: formSessione.data.value,
        ora: formSessione.ora.value,
        tipo: formSessione.tipo.value,
        categoria: categoria === '' ? null : categoria,
        capienza: capienza === '' ? null : Number(capienza),
        allenatoreId: allenatore === '' ? null : Number(allenatore)
    };

    try {
        const creata = await chiamaApi('POST', '/api/sessioni', nuovaSessione);
        mostraMessaggio('Creata la sessione ' + descriviSessione(creata), false);
        formSessione.reset();
        caricaSessioni();
    } catch (errore) {
        mostraMessaggio(errore.message, true);
    }
});

// Prima gli allenatori, così la tabella delle sessioni può già mostrarne i nomi
async function avvia() {
    await caricaAllenatori();
    await caricaSessioni();
}

avvia();

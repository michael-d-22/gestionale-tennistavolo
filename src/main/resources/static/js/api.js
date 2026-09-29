// Funzioni comuni a tutte le pagine

// Chiama l'API REST e restituisce il JSON della risposta.
// Se il server risponde con un errore (400, 404, 409...) lancia un Error
// con il messaggio preparato dal GestoreErrori.
async function chiamaApi(metodo, url, corpo) {
    const opzioni = { method: metodo, headers: {} };
    if (corpo !== undefined) {
        opzioni.headers['Content-Type'] = 'application/json';
        opzioni.body = JSON.stringify(corpo);
    }

    const risposta = await fetch(url, opzioni);

    let dati = null;
    try {
        dati = await risposta.json();
    } catch (e) {
        // la risposta non ha un corpo JSON
    }

    if (!risposta.ok) {
        if (dati && dati.messaggio) {
            throw new Error(dati.messaggio);
        }
        throw new Error('Errore ' + risposta.status);
    }
    return dati;
}

function mostraMessaggio(testo, isErrore) {
    const box = document.getElementById('messaggio');
    box.textContent = testo;
    box.className = isErrore ? 'errore' : 'successo';
}

// Aggiunge una cella a una riga di tabella.
// Uso textContent (e non innerHTML) così un nome come "<b>Rossi</b>" viene mostrato come testo.
function aggiungiCella(riga, testo) {
    const cella = document.createElement('td');
    cella.textContent = (testo === null || testo === undefined) ? '-' : testo;
    riga.appendChild(cella);
    return cella;
}

function creaPulsante(testo, azione) {
    const pulsante = document.createElement('button');
    pulsante.textContent = testo;
    pulsante.addEventListener('click', azione);
    return pulsante;
}

function aggiungiOpzione(select, valore, testo) {
    const opzione = document.createElement('option');
    opzione.value = valore;
    opzione.textContent = testo;
    select.appendChild(opzione);
}

function descriviSessione(sessione) {
    let testo = sessione.data + ' ' + sessione.ora.substring(0, 5) + ' - ' + sessione.tipo;
    if (sessione.categoria) {
        testo += ' ' + sessione.categoria;
    }
    return testo;
}

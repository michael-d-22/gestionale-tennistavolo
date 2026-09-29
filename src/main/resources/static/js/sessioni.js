const formSessione = document.getElementById('form-sessione');
const tabellaSessioni = document.getElementById('tabella-sessioni');

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

    const nuovaSessione = {
        data: formSessione.data.value,
        ora: formSessione.ora.value,
        tipo: formSessione.tipo.value,
        categoria: categoria === '' ? null : categoria,
        capienza: capienza === '' ? null : Number(capienza)
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

caricaSessioni();

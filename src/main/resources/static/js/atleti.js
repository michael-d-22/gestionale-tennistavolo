const formAtleta = document.getElementById('form-atleta');
const tabellaAtleti = document.getElementById('tabella-atleti');

async function caricaAtleti() {
    try {
        const atleti = await chiamaApi('GET', '/api/atleti');
        tabellaAtleti.innerHTML = '';
        for (const atleta of atleti) {
            const riga = document.createElement('tr');
            aggiungiCella(riga, atleta.id);
            aggiungiCella(riga, atleta.cognome);
            aggiungiCella(riga, atleta.nome);
            aggiungiCella(riga, atleta.categoria);
            tabellaAtleti.appendChild(riga);
        }
    } catch (errore) {
        mostraMessaggio(errore.message, true);
    }
}

formAtleta.addEventListener('submit', async function (evento) {
    evento.preventDefault(); // evita che il browser ricarichi la pagina

    const nuovoAtleta = {
        nome: formAtleta.nome.value,
        cognome: formAtleta.cognome.value,
        categoria: formAtleta.categoria.value
    };

    try {
        const creato = await chiamaApi('POST', '/api/atleti', nuovoAtleta);
        mostraMessaggio('Aggiunto ' + creato.nome + ' ' + creato.cognome, false);
        formAtleta.reset();
        caricaAtleti();
    } catch (errore) {
        mostraMessaggio(errore.message, true);
    }
});

caricaAtleti();

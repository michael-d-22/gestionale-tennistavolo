const formAllenatore = document.getElementById('form-allenatore');
const tabellaAllenatori = document.getElementById('tabella-allenatori');

async function caricaAllenatori() {
    try {
        const allenatori = await chiamaApi('GET', '/api/allenatori');
        tabellaAllenatori.innerHTML = '';
        for (const allenatore of allenatori) {
            const riga = document.createElement('tr');
            aggiungiCella(riga, allenatore.id);
            aggiungiCella(riga, allenatore.cognome);
            aggiungiCella(riga, allenatore.nome);
            tabellaAllenatori.appendChild(riga);
        }
    } catch (errore) {
        mostraMessaggio(errore.message, true);
    }
}

formAllenatore.addEventListener('submit', async function (evento) {
    evento.preventDefault();

    const nuovoAllenatore = {
        nome: formAllenatore.nome.value,
        cognome: formAllenatore.cognome.value
    };

    try {
        const creato = await chiamaApi('POST', '/api/allenatori', nuovoAllenatore);
        mostraMessaggio('Aggiunto ' + creato.nome + ' ' + creato.cognome, false);
        formAllenatore.reset();
        caricaAllenatori();
    } catch (errore) {
        mostraMessaggio(errore.message, true);
    }
});

caricaAllenatori();

const sceltaMese = document.getElementById('scelta-mese');
const sceltaAtleta = document.getElementById('scelta-atleta');
const tabellaRiepilogo = document.getElementById('tabella-riepilogo');
const tabellaPresenze = document.getElementById('tabella-presenze');
const risultatoPercentuale = document.getElementById('risultato-percentuale');

// Il campo <input type="month"> usa il formato "2026-10", lo stesso che il server legge come YearMonth
function meseCorrente() {
    const oggi = new Date();
    const mese = String(oggi.getMonth() + 1).padStart(2, '0');
    return oggi.getFullYear() + '-' + mese;
}

function aggiungiRigaRiepilogo(etichetta, valore) {
    const riga = document.createElement('tr');
    const intestazione = document.createElement('th');
    intestazione.textContent = etichetta;
    riga.appendChild(intestazione);
    aggiungiCella(riga, valore);
    tabellaRiepilogo.appendChild(riga);
}

async function calcolaReport() {
    const mese = sceltaMese.value;
    try {
        const riepilogo = await chiamaApi('GET', '/api/report/riepilogo?mese=' + mese);
        tabellaRiepilogo.innerHTML = '';
        aggiungiRigaRiepilogo('Totale allenamenti', riepilogo.totaleSessioni);
        aggiungiRigaRiepilogo('Giorni di allenamento', riepilogo.giorniAllenamento);
        aggiungiRigaRiepilogo('Presenze totali', riepilogo.presenzeTotali);
        aggiungiRigaRiepilogo('Presenze medie al giorno', riepilogo.presenzeMedieAlGiorno);
        aggiungiRigaRiepilogo('Presenze medie per turno', riepilogo.presenzeMediePerTurno);

        const presenze = await chiamaApi('GET', '/api/report/presenze-atleti?mese=' + mese);
        tabellaPresenze.innerHTML = '';
        for (const riga of presenze) {
            const tr = document.createElement('tr');
            aggiungiCella(tr, riga.cognome);
            aggiungiCella(tr, riga.nome);
            aggiungiCella(tr, riga.presenze);
            tabellaPresenze.appendChild(tr);
        }
        mostraMessaggio('Report di ' + mese + ' aggiornato', false);
    } catch (errore) {
        mostraMessaggio(errore.message, true);
    }
}

async function calcolaPercentuale() {
    const url = '/api/report/percentuale-atleta?atletaId=' + sceltaAtleta.value + '&mese=' + sceltaMese.value;
    try {
        const risultato = await chiamaApi('GET', url);
        risultatoPercentuale.textContent = risultato.cognome + ' ' + risultato.nome + ': '
                + risultato.presenze + ' presenze su ' + risultato.sessioniCategoria
                + ' sessioni ' + risultato.categoria + ' = ' + risultato.percentuale + '%';
    } catch (errore) {
        mostraMessaggio(errore.message, true);
    }
}

async function caricaAtleti() {
    try {
        const atleti = await chiamaApi('GET', '/api/atleti');
        for (const atleta of atleti) {
            aggiungiOpzione(sceltaAtleta, atleta.id, atleta.cognome + ' ' + atleta.nome);
        }
    } catch (errore) {
        mostraMessaggio(errore.message, true);
    }
}

document.getElementById('pulsante-calcola').addEventListener('click', calcolaReport);
document.getElementById('pulsante-percentuale').addEventListener('click', calcolaPercentuale);

sceltaMese.value = meseCorrente();
caricaAtleti();
calcolaReport();

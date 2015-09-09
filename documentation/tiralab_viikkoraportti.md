#TiraLab viikkoraportit

##Viikkoraportti #2
(Koodin siivous)
Huomasin A* hakuni nojaavan pahasti Collections.ArrayList:iin, erityisesti solmujen hallinnan osalta. Tähän oli saatava muutos.

Selvitin erilaisia listanjärjestysalgoritmeja ja päädyin lopulta QuickSorttiin (https://en.wikipedia.org/wiki/Quicksort). 500 noden (mittapuussani valtava - jo 100 olisi valtava Mistsin kartalla) listalla 0,7 millisekunttia.
Irroitin sivussa listan PathFinder-luokasta, jotta se olisi kätevämmin käytettävissä muissa mahdollisissa tarkoituksissa (kuten testauksessa).

Haastavinta viikolla oli löytää sopiva sorttausalgoritmi. Itse taulukon luominen ja laajentaminen kävi aika helpolla, joskin yksi pitkään metsästetty bugi vaivasi minua tovin. Olin laittanut noden poistoon suunnilleen seuraavanlaisen koodin:
<pre>
if (index<=num) {
	Node[] newNodeArray = new Node[capacity];
	for (int i = 0; i < num; i++) {
		if (i!=index) {
			newNodeArray[i] = data[i];
		}
	}
	data = newNodeArray;
	this.num--;
}
</pre>
Tuloksena taulukkoon jäi aina tyhjä kolo (kohtaan i), jonka referoiminen aiheutti nullpointereita. Päädyin lisäsäämään looppiin lisäyksissä kasvavan muuttujan(j), jonka mukaan uuteen taulukkoon tavarat laitetaan. 

Lopuksi vielä irroitin Nodet tästä SortedNodeLististä ohjaajan suosituksesta. Nyt SortedList on oma luokkansa, jota SortedNodeList perii ahkerasti. Kattoluokka on näin paljon yleistettävämpi ja sitä voidaan hyödyntää myös reitinhaun ulkopuolella.

Kaikenkaikkiaan kiva ja opettavainen viikko. Ensi viikolla loput Collectionssit hiiteen ja sitten visualisointia tekemään.
Koitan vielä ehtiä tässä tällä viikolla väsätä vähän testejä.

##Viikkoraportti #1
(Aiheen määrittely ja repon perustaminen)

Aiheen valinnassa painiskelin kahden kilpailijan välillä. Toisaalta olisin halunnut jatkaa Mists-pelin kenttägeneraattorin laajentamista, mutta myös reitinhaku houkutti kovasti. Keskusteltuamme asiasta aloitustilaisuudessa vakuutuin kuitenkin reitinhaun olevan parempi vaihtoehto - se on selkeämpi benchmarkattava ja optimoitava kohde.

Sain JavaLabin osuuden pääteltyä pakettiin ja päivitin projectDescriptionit vastaamaan uutta suuntaa. Tarkastin hieman olemassaolevaa toteutusta ja googlettelin erilaisia reitinhakutapoja. Kaikinpuolin aika kevyt viikko työnteon osalta.

Mitään teknisiä haasteita en viikolla kohdannut.
#TiraLab viikkoraportit

##Viikkoraportti #3
(Solmuverkon hoitaminen)

Implementoituani oman versioni PriorityQueuesta (pathfinding.util.ComparingNodeQueue.java) päätin keskittyä hieman suorityskyvyn mittaamiseen. Loin tätä varten testipenkin (pathfinding.util.TestBench.java) jossa mittailla käytännön suoritusaikojen osumista teoreettisiin. Tulokset eivät juurikaan yllättäneet, mutta olivat mielenkiinoisia varmentaa.

###Lisäykset
####ComparingQueue
add() suorituisi O(1) kaikissa tapauksissa, joissa lisätään 0 kokoiseen listaan sen ensimmäinen elementti, mutta koska näin ei kovin usein tapahdu, periytyy add-funktion aikavaatimus melko suoraan findSpot-funktiolta. Tieteen nimissä ajoin kuitenkin 1 kpl lisäyksen 0 -listaan 10 000 kertaa: suoritusajan keskiarvo ~1.25µs.

findSpot(), joka tunkee annetun noden oikeaan paikkaan, toimii karkeasti selitettynä jotakuinkin näin:
<pre>
kohta = solmut.pituus
solmut[kohta] = annettu_solmu; //asetetaan solmu rivin viimeiseksi
while (kohta>0) { //lopetetaan toisto jos päädytään rivin alkuun
	if (solmut[kohta].arvo > solmut[kohta-1].arvo) { //Jos solmu on arvokkaampi kuin edempänä oleva
		solmut[kohta] = solmut[kohta-1] //siirretään se edeltäjä yhdellä taaksepäin
		kohta--; //liikutaan eteenpäin listalla
	} else {
		break; //Koska seuraava on tätä arvokkaampi, olemme nyt oikeassa kolossa - poistutaan siis loopista
	}
	solmut[kohta] = annettu_solmu //Asetetaan solmu tähän oikeaan paikkaansa;
}
</pre>
Käytännössä aikaa kuluu siis 1:sta N:ään toistoa, jossa N on listan pituus. 1 toisto jos annettu solmu on listan huonoin, N toistoa jos annettu solmu päätyy ensimmäiseksi asti. Aikavaatimuksen findSpotille() - ja täten add():lle - pitäisi siis olla O(N). Testipenkki vahvistaa asian:
<pre>
--- exec-maven-plugin:1.2.1:exec (default-cli) @ mists-game ---
CQ ran 100 times with 100 nodes. Meantime: 15.43833µs
CQ ran 100 times with 200 nodes. Meantime: 24.37965µs
CQ ran 100 times with 300 nodes. Meantime: 31.057689999999997µs
CQ ran 100 times with 400 nodes. Meantime: 45.92405µs
CQ ran 100 times with 500 nodes. Meantime: 63.19072µs
CQ ran 100 times with 600 nodes. Meantime: 100.55975µs
CQ ran 100 times with 700 nodes. Meantime: 118.6673µs
CQ ran 100 times with 800 nodes. Meantime: 149.06004000000001µs
CQ ran 100 times with 900 nodes. Meantime: 166.0286µs
CQ ran 100 times with 1000 nodes. Meantime: 168.44367000000003µs
CQ ran 100 times with 2000 nodes. Meantime: 855.2265600000001µs
CQ ran 100 times with 3000 nodes. Meantime: 1537.8591299999998µs
CQ ran 100 times with 4000 nodes. Meantime: 2947.74302µs
CQ ran 100 times with 5000 nodes. Meantime: 4726.22676µs
</pre>

####SortedList
Kuten ComparingQueuessa, myös SortedListissa add() itsessään käy hyvinkin nopeasti. Elementin lisääminen listan loppuun menee aina O(1) ajassa. Sen oikean paikan löytäminen on hieman toinen juttu. QuickSort, jonka add() aina ajaa, perustuu koko listan läpi käymiseen ja paikkojen vaihteluun sen sisällä. Datan partitioiminen mahdollistaa sen, ettei kaikkia alkioita tarvitse verrata toisiinsa (O(n*n)), vaan pystymme pysymään O(n * log n):ssä. Vaikka suoritusajan kasvuvauhti pysyykin logaritmin ansiosta kohtuullisena, kasvaa se jatkuvasti. Testaus vahvistaa tämän:
<pre>
SL ran 100 times with 100 nodes. Meantime: 177.88354999999999µs
SL ran 100 times with 200 nodes. Meantime: 728.40019µs
SL ran 100 times with 300 nodes. Meantime: 1751.9384599999998µs
SL ran 100 times with 400 nodes. Meantime: 3029.90667µs
SL ran 100 times with 500 nodes. Meantime: 4996.36425µs
SL ran 100 times with 600 nodes. Meantime: 7139.7765µs
SL ran 100 times with 700 nodes. Meantime: 9874.43763µs
SL ran 100 times with 800 nodes. Meantime: 13279.98385µs
SL ran 100 times with 900 nodes. Meantime: 17070.27231µs
SL ran 100 times with 1000 nodes. Meantime: 21765.90101µs
SL ran 100 times with 1100 nodes. Meantime: 26258.77761µs
SL ran 100 times with 1200 nodes. Meantime: 31836.11479µs
SL ran 100 times with 1300 nodes. Meantime: 37465.6481µs
SL ran 100 times with 1400 nodes. Meantime: 45236.80932µs
</pre>

22 millisekunttia 1000 noden sorttaukselle alkaa olla jo melkoisesti kun se ajetaan jokaisen add():n yhteydessä.

###Poistot
Poistot toimivat molemmissa listoissa aikavaatimukseltaan identtisesti lisäysten kanssa. ComparingQueue:n aika kuluu siirtäessä häntää poistetun ruudun kohdalle: tehtyjä operaatiota muodostuu 1-N operaatiota, 1 jos poistetaan viimeinen, N jos poistetaan ensimmäinen. SortedList puolestaan ajaa QuickSortin. Eli CQ O(n), SL O(n * log n).

###Päätelmät
Uusi ComparingQueue toimii *huomattavasti* SortedListiäni nopeammin tässä käyttötarkoituksessa. Listan järjestäminen jokaisen lisäyksen yhteydessä on aivan mielipuolista. Voisi olla testaamisen arvoista antaa SortedListille boolean muuttuja "sorted", joka asetetaan epätodeksi aina lisäyksen ja poiston yhdeydessä. Sorttaus, joka tehdään vain katsomisen yhteydessä (jos ja vain jos sorted=false), asettaisi sen todeksi. Tämä siitä syystä, että QuickSort ei ole merkittävästi nopeampi jo sortatulla aineistolla.
Niin tai näin, vaikuttaisi siltä, että CQ on jokatapauksessa parempi työkalu tähän tarkoitukseen.

###Ongelmista
Viikon kohokohta oli kun metsästin PathFinderin bugia. Jostain ihmeen syystä SortedNodeListin siirto omaksi luokakseen johti siihen, että otukset eivät enää osanneet väistää esteitä. Kaivoin läpi koodia tuntitolkulla, kunnes ongelmaksi paljastui se, että olin kommentoinut pois rivin
<pre>
path.addStep(currentNode); //mihin tätä tarvitaan
</pre>
Luotu reitti ei siis saanut ollenkaan askelia sisäänsä, ja otuksille palautui aina (aikakatkaisuun päätyneen reitinhaun jälkeen) tyhjä reitti. Fallsafena toimiva "jos et saa reittiä, mene suoraan kohti kohdetta" toteutui aina.

En vieläkään tiedä miksi olin kommentoinut pois tuon rivin...

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

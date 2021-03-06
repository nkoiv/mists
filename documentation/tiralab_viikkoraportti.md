#TiraLab viikkoraportit
##Viikkoraportti #7
(Viimeiset hionnat)

Demotilaisuuden QuadTree -projekti sai minut miettimään tietorakenteen hyödyntämisestä törmäysten tarkastamisessa. Quad tree paloittelee asiat segmentteihin ja pitää huolen siitä, ettei millään segmentillä ole koskaan liikaa väkeä. Se voisi olla täydellinen tapa selvittää mihin muihin oloihin tulee verrata kun tarkistellaan yhteentörmäyksiä. Pienen googlettelun jälkeen rakensinkin Util -pakettiin QuadTree -luokan MapObjecteja varten. Alustavat testaukset ovat hyvin lupaavia.

Edelleen suurin tehosyöppö törmäyksissä oli NetBeanssin monitorin mukaan muotojen intersecs(), joka jo [javadocsinkin](http://docs.oracle.com/javase/7/docs/api/java/awt/Shape.html) mukaan on raskas operaatio. Sovelsin hommaa hieman ja loin uuden CollisionBox -luokan, joka on spriten mukana liikkuvat minX, minY, maxX ja maxY. Näiden neljän pisteen avulla selvitetään alustava törmäys ennen tarkempaa tarkistelua. Näiden neljän parametrin päivittäminen aina spriten liikkuessa maksaa hieman suorituskykyä, mutta säästö törmäystarkistelupäässä on siihen verrattuna moninkertainen.

Harkittava vaihtoehto on myös testata olemassaolevan CollisionMapin käyttöä, ja tarkistaa vain sen mukaan ympäröivät ruudut. Sitä varten pitäisi tosin generoida myös efektit ja otukset sisältävä CollisionMap, sillä PathFinder käyttää tällä hetkellä structuresOnly -parametria.

Törmäysten jäätyä pienemmäksi ongelmaksi alkoi reitinhaku taas nostaa päätään suhteellisessa tehonkulutuksessa. Sinällään tehokas ja toimiva algoritmi ajetaan nyt pahimmassa tapauksessa 60 (FPS) kertaa sekunnissa per liikkuva olio. Korjaan tuon ongelman pian tallentamalla reitin ja tarkistamalla sen vain tietyissä tapauksissa.

##Viikkoraportti #6
(Esittelykuntoon laitto)

Työstin käyttöliittymää lisäämällä peliin nappulan, joka piirtää kaikki otusten käyttämät reitit näytölle. Tämä paitsi helpotti melkoisesti käyttöliittymätestausta, myös mahdollistaa hieman paremman demotuksen perjantain esittelytilaisuudessa.

Yleisellä tasolla TiraLab-projekti on nyt valmis. Olen siivonnut pois javan valmisluokat reitinhausta, tehnyt ja testannut pohjalle toimivan minimikeon solmuille ja palastellut pääreitinhaun ymmärrettävämpään muotoon. Sivussa tuli tehtyä myös oma versio quicksorttaavasta jonosta ja hashitön priorityque, joille ehkä keksin vielä käyttöä projektin tiimoilta.

Oli mukavan silmiäavartavaa testata näiden eri tietorakenteiden suorituskykyaikoja käytännön tilanteissa. Erityisen hyvän pohjan tekemiselle antoi Mikon toisen viikon palaute, jossa loistava lista muutostarpeita ja suuntia joita ottaa. Sen tukemana työn tahdittaminen kävi helposti, eikä missään vaiheessa ehtinyt tulla sellainen "mitäs nyt sitten" -olo.

##Viikkoraportti #5
(MinHeap ja testejä)

Rakensin vielä QuickSortin ja järjestestyn sarjan kaveriksi heappiin perustuvan MinHeapin. Testaussessioiden jälkeen paljastui että, kuten oletettavaa, tämä kolmas vaihtoehto toimii parhaiten solmujen hallinnoimisessa. Jäin vielä miettimään, josko voisin optimoida ComparingQueueta pointterilla, jota siirtämällä hoitaisin solmujen poiston - OpenNodes käyttötapauksessa kun poisto on poikkeuksetta aina jonon ensimmäinen solmu. Voi olla, että sen valmistuttua jää tehokkaimmaksi vaihtoehdoksi käyttää heapin swim/sink tyyliä ClosedNodes käytössä ja ComparingQueuen pointterin liikuttelua OpenNodesilla.

Kattava testaus osoittautuu hankalaksi. Päivitin util-luokan yksikkötestit käymään läpi kaikki nämä apuluokat, mutta vaikuttaa siltä, että todellista suorituskykyä on kannattavinta mitata [käyttöliittymätestauksen] (https://github.com/nkoiv/mists/blob/master/documentation/game_testing.md) kautta. Niin tai näin, [PIT raportit](https://github.com/nkoiv/mists/tree/master/documentation/pit-reports/) on nyt ajettu ja taas hetken ajan tasalla. Rivikattavuus on util-luokissa 80-90%:sta, mutaatiokattavuuden jäädessä ~40:neen. Hieman vielä tehtävää siellä.

##Viikkoraportti #4
(Siivouksia ja parannuksia)

Kävin erittäin hyvän palautteen innostamana perkaamaan läpi koodia, johon en ollut koskenut toviin. Erotin varsinasen reitinhaun pelin PathFinder -luokasta toteuttamaan uutta PathFindingAlgorithm -interfacea, järjestelin hieman metodeja siistimmiksi ja korjasin pari typerää lapsustani. Node-keskeiset lisäluokat util-paketissa lensivät hiiteen.

Tärkein tekemistäni korjauksista oli (palautteessa huomattu) parin lineaarisen aikavuuden syövän metodin poisto. ComparingNodeQueue ja SortedNodeQueue molemmat kävivät läpi koko helkutin listan aina kun niiltä kysyttiin josko tietyn x ja y -koordinaatin solmu löytyy jo taulusta. Nyt homma hoituu node[][] nodeMap ja int[][] nodeStatus -taulukoilla. Pelkkä nodeMap riittäisi jos node tietäisi onko se auki vai kiinni reitille (boolean open?), mutta jotenkin se ei tunnu solmun itsensä asialta. Homma toimikoon kahden taulun kautta. 

Toinen erittäin osuva palaute koski solmujen (Node.java) depth-parametria: Solmun syvyys kuvaa sitä, kuinka pitkä ketju on kyseisestä nodesta ensimmäiseen. Sitä käytetään suoraan toistaiseksi käytetyn liikkumisen pituuden mittaamiseen. En oikein tiedä mitä olen ajatellut taannoin tuota tehdessäni. Jatkossa depth kertoo toistaiseksi kuljetun matkan hinnan, joka saattaa muuttua kulkijan kykyjen mukaan. Näin eri reittien vertaaminen käy hieman nopeammin ja kevyemmin.

Viikko kului pitkälti koodin siivoamiseen ja lapsusten korjaamiseen uuden tekemisen sijaan. Käyn vielä seuraavaksi läpi minulle asetetun vertaisarvioinnin, jonka jälkeen voin palata uuden koodin kirjoittamiseen.

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
Viikon kohokohta oli kun metsästin PathFinderin bugia. Jostain ihmeen syystä SortedNodeListin siirto omaksi luokakseen johti siihen, että otukset eivät enää osanneet väistää esteitä. Kaivoin läpi koodia pitkään, kunnes ongelmaksi paljastui se, että olin kommentoinut pois rivin
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

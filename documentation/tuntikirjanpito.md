#Viikko 5 (21h)
(UI)

##26.8
* 1h [DOKUMENTOINTI] Päivitetty javadocia
* 2½h [TESTAUS] Conffattu PIT:iä ja kirjoitettu uusia testejä (Pathfinding, MapGen)

##25.8
* ½h [OHJELMOINTI] Luotu ensimmäinen asetuspainike (muteMusic) ja lisätty se alapalkkiin
* 1h [OHJELMOINTI] Muokattu äänentoisto hieman fiksummaksi (myös musiikki nyt soundManagerin alla)

##24.8
* 1h [DOKUMENTOINTI] Päivitetty Project Descriptioniin kohta käytössä olevista lisenssoiduista resursseista
* 2h [OHJELMOINTI] Luotu soundManager-luokka ja otettu se käyttöön
* 1h [OPISKELU] Selvitetty äänentoistoa JavaFX:llä
* 1h [DOKUMENTOINTI] Päivitetty JavaDoc'ia
* 1h [DOKUMENTOINTI] Piirretty lisää sekvenssikaavioita

##23.8
* 1h [DOKUMENTOINTI] Piirretty sekvenssikaavioita
* 2h [OHJELMOINTI] Tehty actionbuttonit ja menu locationiin
* 3h [OHJELMOINTI] Käyty rakentamaan UI:ta ikkunoiden kautta
* 1h [OHJELMOINTI] Rakennettu Controllerit pelin eri tiloille (GameState.class)
* 3h [OPISKELU] Selvitetty erilaisia tapoja muodostaa UI pelille

#Viikko 4 (38h)
(Reitinhaku ja karttojen generointi)

##20.8
* 1½h [TESTAUS] Koitettu saada PITiä skippaamaan luokkia pom.xml:n kautta.
* ½h [OPISKELU] Selvitetty PIT:ssä luokkien testaamatta jättämistä
* 2h [DOKUMENTOINTI] Kirjoitettu JavaDoc'ia
* 5h [OHJELMOINTI] Luotu MapGenerator

##19.8
* 2h [OPISKELU] Koluttu RogueBasinnia (http://roguebasin.roguelikedevelopment.org) ja tutkittu karttojen generoimista
* 7h [OHJELMOINTI] Korjattu pathfinding VIHDOINKIN toimimaan. Ei enää jumitusta nurkissa.

##18.8h
* 3h [OHJELMOINTI] Taisteltu PathFinderin kanssa. Kirjoitettu se jälleen uusiksi

##17.8
* 2h [OHJELMOINTI] Lisätty tilemap

##16.8
* 3h [OHJELMOINTI] Lisätty PathFinderille Clearance-map -mahdollisuus
* 1h [OPISKELU] Etsitty tapoja löytää polku suurille (yli ruudun kokoisille) asioille pathfinderilla

##15.8
* 2h [OHJELMOINTI] Palattu vanhaan PathFinderiin, mutta uuden opeilla
* 3h [OHJELMOINTI] Korvattu PathFinder uudella versiolla
* 1h [OPISKELU] Etsitty parasta tapaa löytää "lähin avoin ruutu" kun kohderuutu on suljettu
* 1h [OHJELMOINTI] Korjattu PathFinderia

##14.8
* 3h [OHJELMOINTI] Rakennettu ensimmäinen PathFinder
* 1h [OPISKELU] Tutkittu erilaisia Pathfinding-algoritmeja

#Viikko 3 (14h)
(Toiminnot ja efektit)

##13.8
* 3h [TESTAUS] Testien rakentaminen taistelulle ja MapObjecteille

##12.8
* 1h [OHJELMOINTI] Tehty POC-action "MeleeAttack" toimivaksi
* 2h [OHJELMOINTI] Rakennettu Efektit
* 1h [OHJELMOINTI] Luotu Actionit

##11.8
* 1h [OHJELMOINTI] Käyty rakentamaan POC "MeleeAttack"iä
* 1h [MÄÄRITTELY] Pohdittu hyökkäysten mallintamista

##10.8
* 1h [OHJELMOINTI] Alettu rakentamaan Action -luokkaa
* 2h [OPISKELU] Tutkittu vaihtoehtoja taistelusysteemiin
* 2h [OHJELMOINTI] Rakennettu MapObjectit käyttämään HashMappiä flageihin

##9.8
* 1h [OHJELMOINTI] Muokattu PlayerCharacter peritytymään Creature -luokasta

#Viikko 2 (7½h)
(Luokkarakenne ja POC)

##6.8
* 1h [TESTAUS] Tehty testit pelaajan liikkumiselle

##3.8
* 3h [TESTAUS] Kirjoitettu testi pelaajan ja rakennusten törmäyksille
* 2h [OPISKELU] Selvitetty JUnit testien tekemistä Canvakselle

##1.8
* ½h [OHJELMOINTI] Korjattu liikkumista
* 1h [PIIRTÄMINEN] Tehty alustavat spritet puuttuville pelaajan puuttuville suunnille (vasen-oikea)

#Viikko 1 (24h)
(Projektin alustus ja suunnittelu)

##30.7
* 2h [OHJELMOINTI] Lisätty animimointi pelaajan liikkeeseen
* 1h [PIIRTÄMINEN] Luotu pari spriteä (Himmu) testausta varten
* 2h [OHJELMOINTI] Lisätty CollisionDetection spriteille
* 2h [OPISKELU] Tutustuttu törmäysten hoitamiseen

##29.7
* 3h [OHJELMOINTI] Rakennettu main loop (animation timer) ja tehty pelaajasta liikuteltava objekti
* 1½h [OHJELMOINTI] Luotu keskeiset luokat Sprite, MapObject, Location) POCia varten
* 2h [OPISKELU] Tutustuttu erilaisiin tapoihin käsitellä Spritejä
* 1h [MÄÄRITTELY] Piirretty alustava luokkakaavio

##28.7
* ½h [OHJELMOINTI] Ensimmäiset rivit koodia (JavaFX-stagen alustus)
* 1h [PIIRTÄMINEN] Väliaikaisten objektien luominen POC'ia varten
* 1h [OPISKELU] JavaFX-tutoriaalie lukemista
* 1h [MÄÄRITTELY] Aihemäärittelyn kirjoittamista
* 1h [OPISKELU] Googlen kautta sopivan kirjaston etsimistä (javaFX?)

##27.7
* 2h [PROJEKTINHALLINTA] Projektin luominen netbeanssilla ja siirtäminen githubiin
* 2h [OPISKELU] Tutustuminen kurssin materiaaleihin
* 1h [MÄÄRITTELY] Aiheen ideointi (graafinen roguelike)
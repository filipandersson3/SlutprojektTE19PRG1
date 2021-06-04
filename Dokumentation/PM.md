# Fraktalvisare

Filip 2021-06-01

## Inledning

Jag hade tänkt göra ett program som kan visualisera mandelbrotmängden genom att köra en iterativ process och se vilka tal som sticker mot 0 och ritar ut dem som pixlar. Talen ska ritas ut som pixlar med färg beroende på hur många iterationer som behövs för att få reda på att det går mot 0. De tal som inte går mot 0 kan ritas med en annan färg.

Mandelbrotmängden räknas ut med den här formeln:

z_(n+1) = (z_n)^2+c

Därför tänker jag att jag måste ha en funktion som tar värdet av samma funktion om och om igen i en loop. Om loopen då blir ett värde som betyder att funktionen går mot 0 så kan jag färga pixeln med en viss färg beroende på hur många iterationer som behövdes för att få reda på att den går mot 0. Annars om loopen kan fortsätta nog många gånger utan att den går mot 0 så ritas den som pixel med annan färg.

Jag borde kunna göra så att programmet har en viss storlek på skärmen så att antalet pixlar är lika många och sen kan det gå igenom och göra beräkningen för alla pixlar från topp till botten.

Jag vill också kunna zooma in på fraktalen genom att ändra var beräkningarna börjar.

## Bakgrund

Jag använde ScreenRenderer mallen för att kunna rita ut pixlar på skärmen.

Första steget var att förstå mallen, hur den funkar och ta bort de delar som jag inte behövde, t.ex. sprites. Sen visste jag att jag skulle gå igenom alla pixlar på skärmen och rita ut dem som färger beroende på mandelbrotmängden. Men då var problemet att jag inte visste hur jag skulle gå igenom alla pixlar på skärmen. Det är så många pixlar på skärmen men jag försökte att rita ut ett litet rutnät för att förstå hur jag kan bestämma x- och y-koordinater på skärmen. Jag hade värdena för längd och bredd för skärmen så jag gjorde att y värdet är den mängd rader neråt som pixeln är på. Alltså blir det heltalsvärdet för den pixelns plats i arrayen delat på bredden av skärmen. X-värdet är då resten kvar efter divisionen för att det är hur många pixlar som är kvar efter man har tagit bort alla hela rader ovanför den. När jag hade x och y kunde jag då rita ut alla pixlar på skärmen som svarta för att testa att den delen fungerar. Hade lite problem för att programmet försökte rendera en pixel för mycket så jag tog WIDTH * HEIGHT delat på scale för att jag trodde att det skulle fixa det, men det gjorde att den bara ritade ut en fjärdedel av skärmen så jag gjorde -1 istället.

När det fungerade försökte jag stoppa in formeln för att beräkna fraktalen i koden. Jag gjorde en iteration som skulle loopa visst många gånger och göra formeln och kolla om absolutbeloppet blev mer än 2 och annars rita ut den som svart. Problemet var att jag hade läst wikipedia artikeln fel och skulle egentligen använda komplexa tal för beräkningen. Så jag gjorde en class för komplexa tal, egenskaper och hur man gör gånger, addition och absolutbelopp för dem. Jag tog också bort scale funktionen för att jag tänkte att den inte skulle behövas, mängden iterationer kan sänkas för prestanda istället. Hade lite problem, komplexa talet resettades inte för varje pixel. Dessutom var c värdet exakt samma som x och y värdena så det gick inte att se någon fraktal för att det fanns ingen där. Jag ändrade så att c värdet var mindre och stoppade in offsets för att kunna se fraktalen.

Jag fick en svart massa med alla tal som tillhör mandelbrotmängden, men jag ville även kunna se hur många iterationer som behövdes för att bestämma att det inte tillhörde fraktalen. Så jag ändrade färgen beroende på hur vilken iteration den är på när den se att det inte är del av fraktalen. Färgerna beter sig lite konstigt om man ökar dem med 1 om och om igen så jag ändrade hue saturation och brightness för att man ska få olika färger och så att färgerna inte loopar runt och så att det ser bättre ut.

Då gjorde jag också en zoom och offset som man kan ändra i koden (genom att dela c med zoom värdet och lägga till offset till c). Jag ändrade också fps till 1 som tillfällig fix för att datorn inte ska försöka rendera tiotals frames av samma bild per sekund.

Sen gjorde jag att man kan kontrollera offset och zoom medans man kör med tangentbordet (skärmen uppdateras också när man trycker). Jag försökte även att optimera programmet genom att ta bort roten ur beräkning, men det blev ingen märkbar skillnad. Därför flyttade jag all beräkning till en ny thread class som jag startade i main. Det gjorde redan att det gick snabbare men jag delade också upp skärmen i flera bitar där varje thread beräknar en egen bit. Antalet threads är också beroende på antalet processorkärnor som är tillgängliga. Då blev det en väldigt stor skillnad och det går faktiskt att använda programmet nu.

Sen gjorde jag fps högre så att man kan se programmet rita ut bilden och gjorde lite mer UI saker. Jag rengjorde också koden och kommenterade det som jag tror är svårt att förstå. Jag hade ett bug när man tryckte för många gånger på tangentbordet för att då skapades bara mer och mer threads så jag satte en gräns där de threads som kör slutar köra.

## Positiva erfarenheter

Det gick bra att:

* Göra som jag hade planerat för att jag försökte att förstå problemet innan jag började koda.
* Göra multithreading för att jag läste koden från vad någon annan hade gjort och försökte att förstå det och sen väntade ett tag för att kunna använda det i mitt egna program. Annars om jag bara kopierade det så hade det inte fungerat och det hade varit svårt att ändra något för att t.ex. fixa en bug om jag inte förstod hur det fungerade.
* Förstå hur mandelbrotmängden fungerar för att det fanns bra källor på internet.
* Skriva klasser och flytta värden mellan dom och det känns som att jag förstår bättre hur man gör nu.
* UI fungerar ganska bra för att jag tänkte att man måste veta hur man använder programmet utan att behöva gå in i källkoden.

## Negativa erfarenheter

Det gick sämre att:

* Få zoomningen att zooma in mot mitten. Kunde göra lite förbättringar men den är inte centrerad. Skulle kunna undvika genom att lägga in någon slags offset som gör att man först går ner en halv skärm och åt höger för att zooma in mot vänstra hörnet och sedan gå en halv skärm upp och åt vänster igen för att zooma mot mitten men vet inte riktigt hur det skulle fungera.
* Cancel knappen fungerar inte i menyn då man startar upp programmet. Den tolkar allt som inte är en int som att den ska starta en ny meny. Skulle ha tänkt på att en JOptionPane kan ge fler värden än man tror.
* Jag gjorde så att om fler än 10 worker threads jobbar samtidigt så stannar dem och startar en ny. Det är inte så bra om man har en dator med fler än 10 kärnor för att då kommer bara en att jobba. Skulle kunna undvika genom att se hur många threads som blir ett problem för en dator och sätta gränsen där.

## Sammanfattning

* Planering gick bra, programmet blev som jag ville och jag hade tid.
* Multithreading gick bra, förstår hur det funkar och det blev mycket snabbare.
* Det gick bra att förstå problemet för att det fanns bra sidor att läsa om mandelbrotmängden på internet
* Skriva klasser och flytta värden mellan dom känns som att jag förstår bättre nu.
* UI fungerar ganska bra för att jag tänkte att folk måste kunna använda det.
* Det gick sämre att få zoomningen att zooma mot mitten, måste förstå hur zooming fungerar nästa gång jag använder det.
* Borde tänka på att JOptionPane måste kunna kryssas ut och cancelas.
* Borde göra program tillgängliga för ännu fler kärnor.

Utvecklingsmöjligheter skulle vara att fixa det som gick mindre bra och se om optimeringen skulle kunna göras bättre. Hade varit roligt om man kunde ha en inställning som gör att mängden iterationer beror på vilken prestanda man får så att programmet inte känns långsamt. Skulle också kunna göra så att man kan välja olika fraktaler, ändrar man bara beräkningen så skulle det nog fungera. Det skulle nog också gå mycket fortare om man gjorde beräkningen på GPU för att då tror jag att man får mycket mer trådar som kan köras samtidigt.

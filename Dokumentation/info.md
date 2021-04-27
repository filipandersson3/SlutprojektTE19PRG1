Här sprar du alla dokumentationsfiler. Skisser på Gui, grafik, flödesdiagram

Jag ska göra ett program som kan visualisera mandelbrot set genom att köra en iterativ process och se vilka tal 
som sticker mot 0 och ritar ut dem som pixlar. Talen ska ritas ut som pixlar med färg beroende på hur många 
iterationer som behövs för att få reda på att det går mot 0. De tal som inte går mot 0 kan ritas med en annan färg.

Mandelbrot set räknas ut med

z_(n+1) = (z_n)^2+c

Därför tänker jag att jag måste ha en funktion som tar värdet av samma funktion om och om igen i en loop.
Om loopen då blir ett värde som betyder att funktionen går mot 0 så kan jag färga pixeln med en viss färg beroende på hur 
många iterationer som behövdes för att få reda på att den går mot 0.
Annars om loopen kan fortsätta nog många gånger utan att den går mot 0 så ritas den som pixel med annan färg.

Jag borde kunna göra så att programmet har en viss storlek på skärmen så att antalet pixlar är lika många och sen kan 
det gå igenom och göra beräkningen för alla pixlar från topp till botten.

Jag vill också kunna zooma in på fraktalen genom att ändra var beräkningarna börjar.

vad 
vilka delar
hur ska programmet fungera
vad vet jag inte
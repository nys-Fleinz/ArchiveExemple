import extensions.CSVFile;

class quiVeutGagnerDesBonbons extends Program {
    final String NOMDUJEU = "Qui veut gagner des bonbons";
    final String locationData = "../ressources/data.csv"; // Location du fichier data.csv
    CSVFile dataCSV = loadCSV(locationData); // A MODIFIER SI UTILISATION DES SCRIPTS .SH
    CSVFile questions = loadCSV("../ressources/questions.csv"); // A MODIFIER SI UTILISATION DES SCRIPTS .SH
    CSVFile eventsCSV = loadCSV("../ressources/events.csv"); // A MODIFIER SI UTILISATION DES SCRIPTS .SH

    // initialiser le tableau de réponses à false pour après savoir si la question a été posée
    void initialiserTableauReponses(boolean[] questionsPosees) {
        for(int i=1; i<length(questionsPosees); i=i+1) {
            questionsPosees[i]=false;
        }
    }

    //Teste la fonction initialiserTableauReponses
    void testInitialiserTableauReponses() {
        boolean[] questionsPosees = new boolean[5];
        initialiserTableauReponses(questionsPosees);
        assertEquals(false, questionsPosees[0]);
        assertEquals(false, questionsPosees[1]);
        assertEquals(false, questionsPosees[2]);
        assertEquals(false, questionsPosees[3]);
        assertEquals(false, questionsPosees[4]);
    }


    //Boucle vérifiant si le string est un nombre en le convertisssant en char puis vérifiant si il est bien entre '0' et '9'
    int requireInt() {
        String input = readString();
        while(length(input)!=1 || !(charAt(input, 0)>='1' && charAt(input, 0)<='9')) {
            print(ANSI_YELLOW+"[⚠️ ]"+ANSI_RED+" Veuillez entrer un nombre entre 1 et 9: "+ ANSI_GREEN);
            input = readString();
        }
        return charToInt(charAt(input, 0));
    }

    Joueur newJoueur(String nom) {
        Joueur joueur = new Joueur();
        joueur.nom = nom;
        return joueur;
    }

    //Teste la fonction newJoueur
    void testNewJoueur() {
        Joueur joueur = newJoueur("Test");
        assertEquals("Test", joueur.nom);
    }


    //Crée le tableau de joueurs à l'aide du nombre de l'entrée utilisateur 
    Joueur[] CreerJoueurs() {
        println(ANSI_BLUE+"[👱] Combien de joueurs êtes-vous?"); //demander le nombre de joueurs
        int nombreJoueurs = requireInt();
        Joueur[] tab = new Joueur[nombreJoueurs];
        for(int i=0; i<nombreJoueurs; i=i+1) {
            print(ANSI_GREEN+"Insérez le nom du joueur numéro "+ANSI_BLUE+(i+1)+ANSI_RESET+": "+ANSI_PURPLE); //demander le nom de chaque joueur numéro i
            tab[i] = newJoueur(readString());
        }
        return tab;
    }

    //Retourner un tableau de String d'une ligne au hasard du fichier events.csv
    String[] getEvent() {
        String[] ligne = new String[columnCount(eventsCSV)];
        int eventRandom = (int) (random()*(rowCount(eventsCSV)-1)+1);
        if(stringToDouble(getCell(eventsCSV, eventRandom, 2))>=random()) {
            for(int i=0; i<length(ligne); i=i+1) {
                ligne[i] = getCell(eventsCSV, eventRandom, i);
            }
        } else {
            ligne[0]="no_event"; //si le random est plus grand que la probabilité, on retourne un tableau avec "no_event"
        }
        return ligne;
    }

    //Teste la fonction getEvent
    void testGetEvent() {
        String[] event = getEvent();
        assertEquals(3, length(event));
    }


    //récupérer un tableau de String d'une ligne du fichier questions.csv
    String[] getQuestion(int numeroQuestion) {
        String[] ligne = new String[columnCount(questions, numeroQuestion)];
        for(int i=0; i<length(ligne); i=i+1) {
            ligne[i] = getCell(questions, numeroQuestion, i);
        }
        return ligne;
    }

    //Teste la fonction getQuestion
    void testGetQuestion() {
        String[] question = getQuestion(0);
        assertEquals("question", question[0]);
        assertEquals("nombre de réponses", question[1]);
    }


    //Poser une question à un joueur
    boolean poserQuestion(Joueur joueur, int numeroQuestion, Joueur[] joueurs) {
        if(joueur.bloque) {
            joueur.bloque=false;
            println(ANSI_RED+"[🚫] "+ANSI_GREEN+joueur.nom+ANSI_RED+" est bloqué pour ce tour.");
            delay(2000);
            return false;
        }

        String[] event = getEvent();
        String[] question = getQuestion(numeroQuestion); //récupérer la question

        int prix = (int) (random()*11)+10; //prix aléatoire entre 10 et 20
        println("[🌀] "+ANSI_GREEN+joueur.nom+ANSI_PURPLE+" à ton tour !"); 
        if(!equals(event[0], "no_event")) { //afficher l'événement si il y en a un
            println("[🎲] "+ANSI_YELLOW+event[0]+" "+ANSI_BLUE+event[1]);
        }

        println(ANSI_CYAN+"🍬 Question à "+prix+" bonbons 🍬\n"+ANSI_RESET); //afficher le prix
        println(ANSI_GREEN+ "[--❓--] "+ANSI_RESET);
        formaterQuestion(question); //formater la question
        println(ANSI_GREEN+ "[--❓--] "+ANSI_RESET);
        
        afficherQuestion(question); //afficher les réponses
        return repondreQuestion(joueur, question, event, prix, joueurs);
    }


    //Formater la question pour l'affichage
    void formaterQuestion(String[] question) {
        // Si deux caractères question[0] sont égaux à par exemple $o, faire un print(ANSI_ORANGE), le o est pour le orange
        for(int i=0; i<length(question[0]); i=i+1) {
            if(equals(substring(question[0], i, i+1), "$")) {
            switch(substring(question[0], i+1, i+2)) { // récupérer le caractère après le $
                case "b": // bleu
                print(ANSI_BLUE);
                break;
                case "p": // violet
                print(ANSI_PURPLE);
                break;
                case "g": // vert
                print(ANSI_GREEN);
                break;
                case "r": // rouge
                print(ANSI_RED);
                break;
                case "c": // cyan
                print(ANSI_CYAN);
                break;
                case "y": // jaune
                print(ANSI_YELLOW);
                break;
                case "R": // fond en rouge
                print(ANSI_RED_BG);
                break;
                case "G": // fond en vert
                print(ANSI_GREEN_BG);
                break;
                case "Y": // fond en jaune
                print(ANSI_YELLOW_BG);
                break;
                case "B": // fond en bleu
                print(ANSI_BLUE_BG);
                break;
                case "P": // fond en violet
                print(ANSI_PURPLE_BG);
                break;
                case "C": // fond en cyan
                print(ANSI_CYAN_BG);
                break;
                case "t": // reset la couleur
                print(ANSI_RESET);
                break;
                case "n": // ajouter un retour à la ligne
                println();
                break;
            }
            i=i+1;
            } else {
                print(substring(question[0], i, i+1));
            }
        }
        println(ANSI_RESET);
    }


    //Afficher les réponses
    void afficherQuestion(String[] question){
        for(int i=0; i<stringToInt(question[1]); i=i+1) {
            print(ANSI_BLUE+"REPONSE "+ANSI_PURPLE+(i+1)+" -> ");
            print(question[i+2]);
            println();
        }
    }


    //Traîter les entrées utilisateurs et quelques affichages
    boolean repondreQuestion(Joueur joueur, String[] question, String[] event, int prix, Joueur[] joueurs) {
        int numeroBonneReponse=stringToInt(question[stringToInt(question[1])+2]); //récupérer le numéro de la bonne réponse en fonction du nombre de réponse
        print(ANSI_BLUE+"\n[🍬] "+ANSI_GREEN+"Numéro de la réponse: "+ANSI_PURPLE);
        int reponse = requireInt();
        boolean resultat;
        if(reponse==numeroBonneReponse) {
            println(ANSI_GREEN+"[✅] Bonne réponse "+ joueur.nom +":) ");
            joueur.points+=prix;
            joueur.bonnesReponses+=1;
            resultat=true;
        } else {
            println(ANSI_RED+"[❌] Mauvaise réponse :("+ANSI_RESET);
            resultat=false;
            if(!(joueur.mauvaisesReponses<=0)) { //permets de ne pas avoir de nombres négatifs
                joueur.mauvaisesReponses+=-1;
            }
            if(!(joueur.vies<=0)) { //permets de ne pas avoir de nombres négatifs
                joueur.vies+=-1;
            }
        }
        appliquerEvent(joueur, event, resultat, prix, joueurs);
        if(joueurElimine(joueur)) {
            println("[☠️] Vous-êtes éliminé.");
        }
        delay(2000);
        return resultat;
    }

    //Afficher les stats joueur
    void printStats(Joueur joueur) {
        println(ANSI_BLUE   + "============================");
        println(ANSI_PURPLE + "📊 Statistiques de " + joueur.nom + " 📊" + ANSI_RESET);
        println(ANSI_BLUE   + "============================" + ANSI_RESET);
        println(ANSI_GREEN  + "[🍬] Points : " + ANSI_YELLOW + joueur.points + ANSI_RESET);
        println(ANSI_GREEN  + "[✅] Bonnes réponses : " + ANSI_YELLOW + joueur.bonnesReponses + ANSI_RESET);
        println(ANSI_GREEN  + "[❌] Mauvaises réponses : " + ANSI_YELLOW + joueur.mauvaisesReponses + ANSI_RESET);
        println(ANSI_GREEN  + "[❤️ ] Vies restantes : " + viesToString(joueur.vies) + ANSI_RESET);
        println(ANSI_BLUE   + "============================" + ANSI_RESET);
    }

    void printTableauScores(Joueur[] joueurs) {
        clearScreen();
        println(ANSI_BLUE + "\n======= Tableau des Scores ========" + ANSI_RESET);
        //AFFICHAGE HEADER
        println(ANSI_YELLOW+"|"+ANSI_PURPLE+" JOUEURS          "+ANSI_YELLOW+" | "+ANSI_RED+"PTS "+ANSI_YELLOW+"| "+ANSI_GREEN+" VIES  "+ANSI_YELLOW+"|");
        // Parcours des joueurs pour afficher leurs stats
        for (int i = 0; i < length(joueurs); i++) {
            Joueur joueur = joueurs[i];
            String nom = joueur.nom;
            int points = joueur.points;
            int vies = joueur.vies;
            // Affichage des stats pour chaque joueur
            println(ANSI_YELLOW+"| "+ANSI_PURPLE+nom + genererCaracteres(18-length(nom), ' ')+
            ANSI_YELLOW+"| "+ANSI_RED+points+genererCaracteres(4-length(""+points), ' ')+ANSI_YELLOW
            +"| "+ viesToString(vies)+genererCaracteres((3-vies)*2, ' ') + ANSI_YELLOW+" |");
        }

        println(ANSI_BLUE + "====================================" + ANSI_RESET);
    }

    //Vérifier si un joueur est éliminé
    boolean joueurElimine(Joueur joueur) {
        boolean vf=false;
        if(joueur.vies<=0) {
            vf=true;
        }
        return vf;
    }

    //Teste la fonction joueurElimine
    void testJoueurElimine() {
        Joueur joueur = new Joueur();
        joueur.vies=0;
        assertEquals(true, joueurElimine(joueur));
        joueur.vies=1;
        assertEquals(false, joueurElimine(joueur));
        joueur.vies=-1;
        assertEquals(true, joueurElimine(joueur));
    }

    //Générer un string avec un nombre de caractères et un caractère donné
    String genererCaracteres(int nombre, char car) {
        String generation="";
        for(int i=0; i<nombre; i=i+1) {
            generation=generation+car;
        }
        return generation;
    }

    //Teste la fonction genererCaracteres
    void testGenererCaracteres() {
        assertEquals("aaaa", genererCaracteres(4, 'a'));
        assertEquals("bbbb", genererCaracteres(4, 'b'));
        assertEquals("cc", genererCaracteres(2, 'c'));
    }


    //Retourner un string avec le nombre de vies por l'affichage
    String viesToString(int nombreDeVies) {
        String affichage="";
        for(int i=0; i<nombreDeVies; i=i+1) {
            affichage=affichage+"❤️ ";
        }
        return affichage;
    }

    //Teste la fonction viesToString
    void testViesToString() {
        assertEquals("❤️ ❤️ ❤️ ", viesToString(3));
        assertEquals("❤️ ❤️ ", viesToString(2));
        assertEquals("❤️ ", viesToString(1));
    }

    // EVENTS
    void appliquerEvent(Joueur joueur, String[] event, boolean resultat, int prix, Joueur[] joueurs) {
        if (!equals(event[0], "no_event")) {
            switch (event[0]) {
                case "Double Points":
                    if (resultat) {
                        joueur.points = joueur.points + prix;
                        println(ANSI_YELLOW + "[💥] Double Points ! " + ANSI_RESET + "Les points de la question précédente sont doublés.");
                    }
                    break;

                case "Question Bonus":
                    if (resultat) {
                        joueur.points = joueur.points + 10;
                        println(ANSI_GREEN + "[✨] Question Bonus ! " + ANSI_RESET + "Tu gagnes 10 points supplémentaires.");
                    }
                    break;

                case "Récupère une Vie":
                    if(joueur.vies<3) {
                        joueur.vies = joueur.vies + 1;
                        println(ANSI_RED + "[❤️ ] Récupère une Vie ! " + ANSI_RESET + "Félicitations, tu récupères une vie !");
                        break;
                    }

                case "Échange de Points":
                    if (!(length(joueurs) == 1)) {
                        int numeroJoueurEchanger = (int) (random() * length(joueurs));
                        if(joueurs[numeroJoueurEchanger] == joueur) {
                            println();
                        }
                        int temp = joueurs[numeroJoueurEchanger].points;
                        joueurs[numeroJoueurEchanger].points = joueur.points;
                        joueur.points = temp;
                        clearScreen();
                        println(ANSI_BLUE + "[🔄] Échange de Points ! " + ANSI_RESET + "Tes points ont été échangés avec " + joueurs[numeroJoueurEchanger].nom + ".");
                        printStats(joueur);
                        printStats(joueurs[numeroJoueurEchanger]);
                        print("Appuyez sur entrée pour continuer...");
                        readString();
                    }
                    break;

                case "Bloque Ton Adversaire":
                    if (!(length(joueurs) == 1)) {
                        println("Choisis un adversaire à bloquer:");
                        String listeJoueurs = "";
                        for (int i = 0; i < length(joueurs); i = i + 1) {
                            if (!equals(joueurs[i].nom, joueur.nom)) {
                                listeJoueurs = listeJoueurs + " [" + (i + 1) + "] " + joueurs[i].nom + " ";
                            }
                        }
                        println(listeJoueurs);
                        print("Numéro du joueur à bloquer: ");
                        int numeroJoueurBloque = readInt() - 1;
                        joueurs[numeroJoueurBloque].bloque = true;
                        println(ANSI_BLUE + "[🚫] Bloque Ton Adversaire ! " + ANSI_RED + joueurs[numeroJoueurBloque].nom + ANSI_BLUE + " est bloqué pour un tour." + ANSI_RESET);
                    }
                    break;

                case "Immunité":
                    if (!resultat) {
                        joueur.vies = joueur.vies + 1;
                        println(ANSI_CYAN + "[🛡️] Immunité ! " + ANSI_RESET + "Tu ne perds pas de vie ce tour.");
                    }
                    break;

                case "Mort instantanée":
                    if (!resultat) {
                        joueur.vies = 0;
                        println(ANSI_RED + "[☠️] Mort instantanée ! " + ANSI_RESET + "Tu es éliminé !");
                    }
                    break;

                case "Gain Surprise":
                    int pointsGagnes = (int) (random() * 3) + 1; // Gain aléatoire entre 1 et 3
                    joueur.points = joueur.points + pointsGagnes;
                    println(ANSI_GREEN + "[🎁] Gain Surprise ! " + ANSI_RESET + "Tu gagnes " + pointsGagnes + " points.");
                    break;

                case "Question Fatale":
                    if (!resultat) {
                        joueur.vies = joueur.vies - 2;
                        println(ANSI_RED + "[☠️] Question Fatale ! " + ANSI_RESET + "Une seule erreur et tu perds 2 vies !");
                    }
                    break;
            }
        }
    }

    int donnerQuestion(boolean[] questionsPosees) {
        int i=0;
        //vérifier combien de questions ont déjà été posées
        while(i<length(questionsPosees) && questionsPosees[i]) {
            if(questionsPosees[i]) {
                println("Question "+i+" posée");
                i=i+1;
            }
        }

        //si toutes les questions ont été posées, réinitialiser le tableau
        if(i==length(questionsPosees)) {
            initialiserTableauReponses(questionsPosees);
            return donnerQuestion(questionsPosees);
        }

        int numeroQuestion = (int) (random()*rowCount(questions));

        // générer un numéro de question aléatoire et à partir de cette question, parcourir de 1 en 1 jusqu'à trouver une question non posée, mettre le compteur à 0 quand on arrive à la fin du tableau
        // vérifier que la question n'est pas égale à 0, car c'est l'entête du fichier

        while(questionsPosees[numeroQuestion] || numeroQuestion == 0) {
            numeroQuestion = numeroQuestion + 1;
            if(numeroQuestion == length(questionsPosees)) {
            numeroQuestion = 1;
            }
        }

        questionsPosees[numeroQuestion]=true;
        return numeroQuestion;
    }

    boolean partieTerminee(Joueur[] joueurs) {
        boolean termine=false;
        int compteur=0;
        int elimines=0;
        while(compteur<length(joueurs) && !termine) {
            if(joueurElimine(joueurs[compteur])) {
                elimines=elimines+1;
            }
            if(joueurs[compteur].bonnesReponses>=10) {
                termine=true;
            }
            compteur=compteur+1;
        }

        if(compteur==elimines) {
            termine=true;
        }
        return termine;
    }

    void tour(Joueur[] joueurs, boolean[] questionsPosees) {
        for(int i=0; i<length(joueurs); i=i+1) {
            if(!joueurElimine(joueurs[i])) {
                clearScreen();
                poserQuestion(joueurs[i], donnerQuestion(questionsPosees), joueurs);
            }
        }
    }


    void sauvergarderData(Joueur[] joueurs) {
        String[][] data = new String[rowCount(dataCSV)+length(joueurs)][columnCount(dataCSV)];
        loadCSVString(data, dataCSV);
        ajouterJoueurData(joueurs, data);
        saveCSV(data, locationData);
        dataCSV = loadCSV(locationData);
    }

    void loadCSVString(String[][] data, CSVFile csv) {
        for(int i=0; i<rowCount(csv); i++) {
            for(int j=0; j<columnCount(csv); j++) {
                data[i][j] = getCell(csv, i, j);
            }
        }
    }

    void ajouterJoueurData(Joueur[] joueurs, String[][] data) {
        for(int i=0; i<length(joueurs); i++) {
            data[rowCount(dataCSV)+i][0] = ""+joueurs[i].nom;
            data[rowCount(dataCSV)+i][1] = ""+joueurs[i].points;
        }
    }

    //Afficher les joueurs avec leurs scores
    void afficherData() {
        for(int i=1; i<rowCount(dataCSV); i=i+1) {
            println(ANSI_PURPLE+" ["+ANSI_GREEN+i+ANSI_PURPLE+"]"+ANSI_BLUE+getCell(dataCSV, i, 0)+ANSI_PURPLE+" : "+ANSI_BLUE+getCell(dataCSV, i, 1)+ANSI_RESET);
        }
        println("Appuyez pour continuer...");
    }


    void algorithm() {
        clearScreen();
        println(ANSI_BLUE + "[" + "🎮" + ANSI_BLUE + "] " + ANSI_GREEN + "Bienvenue dans '" + NOMDUJEU + "'\n" + ANSI_RESET);
        println(ANSI_BLUE + "[" + "📜" + ANSI_BLUE + "] " + ANSI_YELLOW + "Règle 1: Chaque joueur commence avec 3 vies." + ANSI_RESET);
        println(ANSI_BLUE + "[" + "🍬" + ANSI_BLUE + "] " + ANSI_YELLOW + "Règle 2: Une bonne réponse donne des points, une mauvaise fait perdre une vie." + ANSI_RESET);
        println(ANSI_BLUE + "[" + "✨" + ANSI_BLUE + "] " + ANSI_YELLOW + "Règle 3: Atteignez 10 bonnes réponses pour gagner !" + ANSI_RESET);
        println(ANSI_BLUE + "[" + "🎲" + ANSI_BLUE + "] " + ANSI_YELLOW + "Règle 4: Certains tours incluent des bonus aléatoires !" + ANSI_RESET);
        println(ANSI_BLUE + "[" + "💔" + ANSI_BLUE + "] " + ANSI_RED + "Règle 5: Si vous perdez vos 3 vies, vous êtes éliminé." + ANSI_RESET);
        println(ANSI_BLUE + "[" + "🏆" + ANSI_BLUE + "] " + ANSI_PURPLE + "Bonne chance et amusez-vous bien !\n\n" + ANSI_RESET);

        // INITIALISER JOUEURS
        Joueur[] joueurs = CreerJoueurs();
        
        // INITIALISER DATA
        boolean[] questionsPosees = new boolean[rowCount(questions)];
        initialiserTableauReponses(questionsPosees);

        while(!partieTerminee(joueurs)) {
            tour(joueurs, questionsPosees);
            printTableauScores(joueurs);
            print("\nAppuyez pour continuer...");
            readString();
        }
        sauvergarderData(joueurs);
        menu();
    }


    void menu() {
        while(true) {
            clearScreen();
            println("Voulez vous relancer une partie ou afficher les scores?\n");
            println(ANSI_GREEN+"["+ANSI_BLUE+"1"+ANSI_GREEN+"] Relancer une partie");
            println(ANSI_YELLOW+"["+ANSI_BLUE+"2"+ANSI_YELLOW+"] Afficher les scores");
            println(ANSI_RED+"["+ANSI_BLUE+"3"+ANSI_RED+"] Quitter\n");
            print(ANSI_PURPLE+"Votre choix: "+ANSI_RESET);
            int choix = requireInt();
            switch(choix) {
                case 1:
                    algorithm();
                    break;
                case 2:
                    afficherData();
                    break;
                case 3:
                    break;
            }
        }
    }
}
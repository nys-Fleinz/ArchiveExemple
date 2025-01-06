import extensions.CSVFile;
import extensions.File;


class quiVeutGagnerDesBonbons extends Program {
    final String nomDuJeu = "Qui veut gagner des bonbons";
    CSVFile questions = loadCSV("../ressources/questions.csv"); // A MODIFIER SI UTILISATION DES SCRIPTS .SH
    CSVFile eventsCSV = loadCSV("../ressources/events.csv"); // A MODIFIER SI UTILISATION DES SCRIPTS .SH


    void initialiserTableauReponses(boolean[] questionsPosees) {
        for(int i=1; i<length(questionsPosees); i=i+1) {
            questionsPosees[i]=false;
        }
    }


    Joueur newJoueur(String nom) {
        Joueur joueur = new Joueur();
        joueur.nom = nom;
        return joueur;
    }


    //Crée le tableau de joueurs à l'aide du nombre de l'entrée utilisateur 
    Joueur[] CreerJoueurs() {
        println("Combien de joueurs êtes-vous?"); //demander le nombre de joueurs
        int nombreJoueurs = readInt();
        Joueur[] tab = new Joueur[nombreJoueurs];
        for(int i=0; i<nombreJoueurs; i=i+1) {
            println("Insérez le nom du joueur numéro "+ANSI_BLUE+(i+1)+ANSI_RESET+": "); //demander le nom de chaque joueur numéro i
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

    //récupérer un tableau de String d'une ligne du fichier questions.csv
    String[] getQuestion(int numeroQuestion) {
        String[] ligne = new String[columnCount(questions, numeroQuestion)];
        for(int i=0; i<length(ligne); i=i+1) {
            ligne[i] = getCell(questions, numeroQuestion, i);
        }
        return ligne;
    }

    boolean poserQuestion(Joueur joueur, int numeroQuestion, Joueur[] joueurs) {
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

    void afficherQuestion(String[] question){
        String header = "";
        String reponses ="";
        int position = 0;

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
        int reponse = readInt();
        boolean resultat;
        if(reponse==numeroBonneReponse) {
            println(ANSI_GREEN+"[✅] Bonne réponse :) "+joueur.nom);
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
        println(ANSI_GREEN  + "[❤️] Vies restantes : " + viesToString(joueur.vies) + ANSI_RESET);
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

    boolean joueurElimine(Joueur joueur) {
        boolean vf=false;
        if(joueur.vies<=0) {
            vf=true;
        }
        return vf;
    }

    String genererCaracteres(int nombre, char car) {
        String generation="";
        for(int i=0; i<nombre; i=i+1) {
            generation=generation+car;
        }
        return generation;
    }


    //Retourner un string avec le nombre de vies por l'affichage
    String viesToString(int nombreDeVies) {
        String affichage="";
        for(int i=0; i<nombreDeVies; i=i+1) {
            affichage=affichage+"❤️";
        }
        return affichage;
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
                    joueur.vies = joueur.vies + 1;
                    println(ANSI_RED + "[❤️] Récupère une Vie ! " + ANSI_RESET + "Félicitations, tu récupères une vie !");
                    break;

                case "Échange de Points":
                    if (!(length(joueurs) == 1)) {
                        int numeroJoueurEchanger = (int) (random() * length(joueurs));
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





    void algorithm() {
        clearScreen();
        println(ANSI_BLUE + "[" + "🎮" + ANSI_BLUE + "] " + ANSI_GREEN + "Bienvenue dans '" + nomDuJeu + "'\n" + ANSI_RESET);
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
    }
}
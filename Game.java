/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael KÃ¶lling and David J. Barnes
 * @version 2011.07.31
 */

public class Game 
{
    private Parser parser;
    private Room currentRoom;
        
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        Room celdaJugador, bloqueCeldas, celdaA, celdaB, pasilloCentral, cocina, moduloAislamiento, comedor,
        pasilloEste, pasilloOeste, patio, taller, enfermeria, baños, lavanderia, tunel, salidaSecreta,
        moduloIngresos, entrada;
      
        // create the rooms
        celdaJugador = new Room("en tu celda");
        bloqueCeldas = new Room("en el bloque de celdas");
        celdaA = new Room("en una pequeña celda");
        celdaB = new Room("en una celda espaciosa");
        pasilloCentral = new Room("en un largo pasillo");
        cocina = new Room("en la cocina");
        moduloAislamiento = new Room("en el modulo de aislamiento");
        comedor = new Room("en el comedor");
        pasilloEste = new Room("en un pasillo estrecho");
        pasilloOeste = new Room("en un pasillo");
        patio = new Room("en el patio");
        taller = new Room("en el taller");
        enfermeria = new Room("en la enfermeria");
        baños = new Room("en los baños");
        lavanderia = new Room("en la lavanderia");
        tunel = new Room("en un pequeño tunel");
        salidaSecreta = new Room("en una salida a la parte trasera de la prision");
        moduloIngresos = new Room("en el modulo de ingresos");
        entrada = new Room("en la entrada de la prision");
        
        // initialise room exits
        //Los parametros van por este orden (norte, este, sur, oeste)
        celdaJugador.setExits(null, null, bloqueCeldas, null);
        bloqueCeldas.setExits(celdaJugador, celdaB, pasilloCentral, celdaA);
        pasilloCentral.setExits(bloqueCeldas, cocina, comedor, moduloAislamiento);
        comedor.setExits(pasilloCentral, pasilloEste, moduloIngresos, pasilloOeste);
        moduloIngresos.setExits(comedor, null, entrada, null);
        celdaA.setExits(null, bloqueCeldas, null, null);
        celdaB.setExits(null, null, null, bloqueCeldas);
        cocina.setExits(null, null, null, pasilloCentral);
        moduloAislamiento.setExits(null, pasilloCentral, null, null);
        pasilloEste.setExits(null, patio, taller, comedor);
        taller.setExits(pasilloEste, null, null, null);
        patio.setExits(null, null, null, pasilloEste);
        pasilloOeste.setExits(null, comedor, enfermeria, baños);
        enfermeria.setExits(pasilloOeste, null, null, null);
        lavanderia.setExits(baños, null, null, null);
        baños.setExits(tunel, pasilloOeste, lavanderia, null);
        tunel.setExits(salidaSecreta, null, null, null);
        salidaSecreta.setExits(null, null, tunel, null);
        celdaB.setExits(moduloIngresos, null, null, null);

        currentRoom = celdaJugador;  // empieza el juego en la celda del jugador
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Bienvenido a World of Zuul!");
        System.out.println("World of Zuul es un nuevo, increiblemente aburrido juego de aventura.");
        System.out.println("Escribe 'help' si necesitas ayuda.");
        System.out.println();
        printLocationInfo();
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            goRoom(command);
        }
        else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }

        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Tus comandos son:");
        System.out.println("   go quit help");
    }

    /** 
     * Try to go in one direction. If there is an exit, enter
     * the new room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = null;
        if(direction.equals("north")) {
            nextRoom = currentRoom.northExit;
        }
        if(direction.equals("east")) {
            nextRoom = currentRoom.eastExit;
        }
        if(direction.equals("south")) {
            nextRoom = currentRoom.southExit;
        }
        if(direction.equals("west")) {
            nextRoom = currentRoom.westExit;
        }

        if (nextRoom == null) {
            System.out.println("No hay salida!");
        }
        else {
            currentRoom = nextRoom;
            printLocationInfo();
        }
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
    
    private void printLocationInfo() {
        System.out.println("Estás " + currentRoom.getDescription());
        System.out.print("Salidas: ");
        if(currentRoom.northExit != null) {
            System.out.print("north ");
        }
        if(currentRoom.eastExit != null) {
            System.out.print("east ");
        }
        if(currentRoom.southExit != null) {
            System.out.print("south ");
        }
        if(currentRoom.westExit != null) {
            System.out.print("west ");
        }
        System.out.println();
    }
}

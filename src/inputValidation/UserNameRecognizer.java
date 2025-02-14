package inputValidation;

public class UserNameRecognizer {
    /**
     * <p> Title: FSM-translated UserNameRecognizer. </p>
     * 
     * <p> Description: A demonstration of the mechanical translation of Finite State Machine 
     * diagram into an executable Java program using the UserName Recognizer. The code 
     * detailed design is based on a while loop with a select list.</p>
     * 
     * <p> Copyright: Lynn Robert Carter © 2024 </p>
     * 
     * @author Lynn Robert Carter
     * 
     * @version 1.00		2024-09-13	Initial baseline derived from the Even Recognizer
     * @version 1.01		2024-09-17	Correction to address UNChar coding error, improper error
     * 									message, and improve internal documentation
     * @version 1.02		2025-01-22	Update FSM to enforce new username rules
     * 
     */

    public static String userNameRecognizerErrorMessage = "";	// The error message text
    public static String userNameRecognizerInput = "";			// The input being processed
    public static int userNameRecognizerIndexofError = -1;		// The index of error location
    private static int state = 0;						// The current state value
    private static int nextState = 0;					// The next state value
    private static boolean finalState = false;			// Is this state a final state?
    private static String inputLine = "";				// The input line
    private static char currentChar;					// The current character in the line
    private static int currentCharNdx;					// The index of the current character
    private static boolean running;						// The flag that specifies if the FSM is running
    private static int userNameSize = 0;				// A username may not exceed 16 characters

    private static void displayDebuggingInfo() {
        if (currentCharNdx >= inputLine.length())
            System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
                    ((finalState) ? "       F   " : "           ") + "None");
        else
            System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
                ((finalState) ? "       F   " : "           ") + "  " + currentChar + " " + 
                ((nextState > 99) ? "" : (nextState > 9) || (nextState == -1) ? "   " : "    ") + 
                nextState + "     " + userNameSize);
    }

    private static void moveToNextCharacter() {
        currentCharNdx++;
        if (currentCharNdx < inputLine.length())
            currentChar = inputLine.charAt(currentCharNdx);
        else {
            currentChar = ' ';
            running = false;
        }
    }

    public static String checkForValidUserName(String input) {
        if (input.length() <= 0) {
            userNameRecognizerIndexofError = 0;
            return "\nERROR: The input is empty"; //clean up the asterisks
        }

		// The local variables used to perform the Finite State Machine simulation
		state = 0;							// This is the FSM state number
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		currentChar = input.charAt(0);		// The current character from above indexed position

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state
        
        userNameRecognizerInput = input;
        running = true;
        nextState = -1;
        System.out.println("\nCurrent Final Input  Next  Date\nState   State Char  State  Size");

        userNameSize = 0;

        while (running) {
            switch (state) {
                case 0: 
                    if (Character.isLetter(currentChar)) { // Must start with a letter 
                        nextState = 1;
                     // Count the character
                        userNameSize++;
                    } else {
                        running = false; //(cleaned up the code and used built in java features for this entire section)
                    }
                    break;

                case 1: 
                    if (Character.isLetterOrDigit(currentChar)) { // Alphanumeric characters
                        nextState = 1;
                        userNameSize++;
                     // . -> State 2
                    } else if (currentChar == '.' || currentChar == '-' || currentChar == '_') { // Allowed symbols
                        nextState = 2;
                        userNameSize++;
                    } else {
                        running = false;
                    }
                 // The execution of this state is finished
    				// If the size is larger than 16, the loop must stop
                    if (userNameSize > 16) 
                        running = false;
                    break;

                case 2: //state 2 deals with checking alphanumeric after a special character
                    if (Character.isLetterOrDigit(currentChar)) { // Alphanumeric after a symbol
                        nextState = 1;
                        userNameSize++;
                    } else {
                        running = false;
                    }

                    if (userNameSize > 16)
                        running = false;
                    break;

                default:
                    running = false;
                    break;
            }

            if (running) {
				displayDebuggingInfo();
				// When the processing of a state has finished, the FSM proceeds to the next
				// character in the input and if there is one, it fetches that character and
				// updates the currentChar.  If there is no next character the currentChar is
				// set to a blank.
				moveToNextCharacter();

				// Move to the next state
				state = nextState;
				
				// Is the new state a final state?  If so, signal this fact.
				if (state == 1) finalState = true;

				// Ensure that one of the cases sets this to a valid value
				nextState = -1;
			}
			// Should the FSM get here, the loop starts again
	
		}
		displayDebuggingInfo();
		
		System.out.println("The loop has ended.");
		
		// When the FSM halts, we must determine if the situation is an error or not.  That depends
		// of the current state of the FSM and whether or not the whole string has been consumed.
		// This switch directs the execution to separate code for each of the FSM states and that
		// makes it possible for this code to display a very specific error message to improve the
		// user experience.
		userNameRecognizerIndexofError = currentCharNdx;	// Set index of a possible error;
		userNameRecognizerErrorMessage = "\nERROR: ";
		
		// The following code is a slight variation to support just console output.
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			userNameRecognizerErrorMessage += "A UserName must start with A-Z, a-z.\n";
			return userNameRecognizerErrorMessage;

		case 1:
			// State 1 is a final state.  Check to see if the UserName length is valid.  If so we
			// we must ensure the whole string has been consumed.

			if (userNameSize < 4) {
				// UserName is too small
				userNameRecognizerErrorMessage += "A UserName must have at least 4 characters.\n";
				return userNameRecognizerErrorMessage;
			}
			else if (userNameSize > 16) {
				// UserName is too long
				userNameRecognizerErrorMessage += 
					"A UserName must have no more than 16 character.\n";
				return userNameRecognizerErrorMessage;
			}
			else if (currentCharNdx < input.length()) {
				// There are characters remaining in the input, so the input is not valid
				userNameRecognizerErrorMessage += 
					"The UserName contains invalid characters. (allowed characters: _ , - , . )\n";
				return userNameRecognizerErrorMessage;
			}
			else {
					// UserName is valid
					userNameRecognizerIndexofError = -1;
					userNameRecognizerErrorMessage = "";
					return userNameRecognizerErrorMessage;
			}

		case 2:
			// State 2 is not a final state, so we can return a very specific error message
			userNameRecognizerErrorMessage +=
				"A UserName character after a symbol must be alphanumeric (A-Z, a-z, 0-9).\n";
			return userNameRecognizerErrorMessage;
			
		default:
			// This is for the case where we have a state that is outside of the valid range.
			// This should not happen
			return "";
		}
	}
}
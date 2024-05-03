package model.exceptions;

//Indicates the operations tried to access a square that is not on the board

public class BoardSquareOutOfBoundsException extends Exception {

    private static final String ERROR_MESSAGE = "select proper squares";


    public BoardSquareOutOfBoundsException() {
        super();
    }

    @Override
    public String getMessage() {
        return ERROR_MESSAGE;
    }
}

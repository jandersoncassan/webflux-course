package br.com.jande.webfluxcourse.service.exception;

public class ObjectNotFoundException extends RuntimeException{

    public ObjectNotFoundException(String message){
        super(message);
    }

}

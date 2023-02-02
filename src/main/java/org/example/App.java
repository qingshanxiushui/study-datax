package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Hello world!
 *
 */
public class App 
{
    public static final Logger LOGGER= LoggerFactory.getLogger(App.class);
    public static void main( String[] args )
    {

        System.out.println( "Hello World!" );
        LOGGER.info("No hook invoked, because base dir not exists or is a file: " );
    }
}

/**
 * Author: Mugi
 * https://github.com/samuelmugi
 * User:mugimacharia
 * Date:23/08/2024
 * Time:15:45
 */

package com.mugi.finsense.exception;

public class ResourceNotFoundException  extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ahorcado;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Andres Moreno Blanc
 * Clase cliente que envia solicitudes al servidor y recibe las respuestas a las mismas
 * 
 */

public class Cliente {
    
    // Dirección del servidor
    static final String HOST = "localhost";

    // Puerto en el que el servidor está escuchando
    static final int Puerto = 2000;
    
    public Cliente(){
        try{

            // Objeto Scanner para la entrada del usuario
            Scanner teclado = new Scanner(System.in);
            
            // Crear socket y flujos de entrada/salida
            Socket sCliente = new Socket(HOST, Puerto);
            DataInputStream flujo_entrada = new DataInputStream(sCliente.getInputStream());
            DataOutputStream flujo_salida = new DataOutputStream(sCliente.getOutputStream());
            
            // Solicitar al cliente que introduzca la dificultad del juego
            String leerDificultad = flujo_entrada.readUTF();
            System.out.print(leerDificultad);
            String dificultad = teclado.next();
            flujo_salida.writeUTF(dificultad);
            
            // Solicitar al cliente una letra tras mostrar palabra oculta 
            String palabraOculta = flujo_entrada.readUTF();
            System.out.println(palabraOculta);
            String pedirLetra = flujo_entrada.readUTF();
            System.out.print(pedirLetra);
            String letra = teclado.next();
            flujo_salida.writeUTF(letra);
            
            
        }catch (Exception e) {
            // Manejar excepciones
            System.out.println(e.getMessage());
        } 
        
    }
        
    public static void main(String[] arg) {
        // Iniciar el cliente
        new Cliente();
    }
    
}

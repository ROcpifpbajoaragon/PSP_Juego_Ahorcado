/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ahorcado;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Andres Moreno Blanc
 * Clase que maneja el servidor, encargado de recibir las solicitudes de los clientes 
 * efectuar las gestiones de estas solicitudes y devolver respuesta al cliente
 */

public class Servidor extends Thread { // extend Thread permite trabajar al servidor con varios clientes diferenciados en hilos
    
    // Declaracion de variables 
    Socket skCliente;
    static final int Puerto = 2000;
    // Declaramos las palabras en funcion del nivel seleccionado
    String[] palabrasFaciles = {"TOMATE","AMIGO","PERRO","GATO","RANA","PASTA","VASO","BOLI","TELE","PATATA"};
    String[] palabrasMedias = {"CABEZAZO","JUGADORES","PROGRAMACION","ALTAVOCES","BICICLETA","COLGADOR","CARGADOR","CARRITO","MECHEROS","EXAMENES"};
    String[] palabrasDificiles = {"AYUNTAMIENTO","SUBCONTRATAR","ESCAULLIRSE","ESTABLECERSE","ZIGZAGUEABAIS","DESINCORPORAR","JAZZ","ELECTROENCEFALOGRAMA","OTORRINOLARINGOLOGO","FOTOSINTESIS"};
    String palabraElegida, palabraOculta;
    List<String> letrasErroneas = new ArrayList<>();
    int intentos = 10;
    int aciertos = 0;
    boolean correcto = false;
    
    // Constructor que recibe el socket del cliente al que se va a atender
    public Servidor(Socket sCliente) {
        skCliente = sCliente;
    }
    
    // Método principal del servidor
    public static void main(String[] arg) {
        try {
            
            // Inicio el servidor en el puerto
            ServerSocket skServidor = new ServerSocket(Puerto);
            System.out.println("Escucho el puerto " + Puerto);

            while (true) {
                // Se conecta un cliente
                Socket skCliente = skServidor.accept();
                System.out.println("Cliente conectado");

                // Atiendo al cliente mediante un thread
                new Servidor(skCliente).start();
            }
        } catch (Exception e) {
            // Manejar excepciones
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
    
    public void run(){
        
        try {
            
            // Creo los flujos de entrada y salida para comunicarme con el cliente
            DataInputStream flujo_entrada = new DataInputStream(skCliente.getInputStream());
            DataOutputStream flujo_salida = new DataOutputStream(skCliente.getOutputStream());
            
            // Solicitar al cliente que introduzca el nivel de dificultad
            flujo_salida.writeUTF("Selecciona la dificultad(facil, medio, dificil): ");
            String nivel = flujo_entrada.readUTF();
            System.out.println("\tEl cliente ha dicho " + nivel);
            
            // En funcion de la opcion elegida por el cliente elegimos una palabra u otra
            if(nivel.equals("facil")){
                palabraElegida = crearPalabraAleatoria(palabrasFaciles);
                flujo_salida.writeUTF(palabraElegida);
            }else if(nivel.equals("medio")){
                palabraElegida = crearPalabraAleatoria(palabrasMedias);
                flujo_salida.writeUTF(palabraElegida);
            }else if(nivel.equals("dificil")){
                palabraElegida = crearPalabraAleatoria(palabrasDificiles);
                flujo_salida.writeUTF(palabraElegida);
            }
            
            // Creamos una palabra oculta en funcion de la elegida 
            palabraOculta = crearPalabraOculta(palabraElegida);
            
            // Creamos un bucle que de 10 intentos al cliente 
            for(intentos=10; intentos>=0; intentos--){
 
                // Dar al cliente a elegir entre letra o palabra 
                flujo_salida.writeUTF("Quieres probar una letra o una palabra: ");
                String opcion = flujo_entrada.readUTF();
                System.out.println("\tEl cliente ha dicho " + opcion);
                flujo_salida.writeUTF("Te quedan " + intentos + " intentos");
                
                if(opcion.equals("letra")){
                    
                    // Si a elegido letra mostramos palabra oculta y pedimos letra
                    flujo_salida.writeUTF(palabraOculta);
                    flujo_salida.writeUTF("Quedan " + intentos + " intentos. Introduce una letra: ");
                    String letra = flujo_entrada.readUTF();
                    System.out.println("\tEl cliente ha dicho " + letra);
                    
                    // Una vez recibimos la letra operamos para saber si esta y modificar palabra oculta
                    palabraOculta = palabraIntermedia(palabraElegida, palabraOculta, letra, aciertos);
                    flujo_salida.writeUTF(palabraOculta);
                    
                    // Si la letra completa la palabra
                    if(palabraOculta.equals(palabraElegida)){
                        
                        flujo_salida.writeUTF(palabraOculta);
                        flujo_salida.writeUTF("HAS ACERTADO LA PALABRA. FELICIDADES!");
                        
                    }
                    
                    // Si la letra no esta o esta pero no completa
                    if(letraEsta(palabraElegida, letra)){
                                    
                        // Si la letra esta pero no completa palabra 
                        flujo_salida.writeUTF("Letra encontrada");
                        flujo_salida.writeUTF(palabraOculta);
                        flujo_salida.writeUTF("Letras erroneas: " + letrasErroneas);
                        intentos++;
                        flujo_salida.writeInt(intentos);
                                    
                    }else{
                                    
                        // Si la letra no esta en la palabra 
                        flujo_salida.writeUTF("Letra no encontrada");
                        flujo_salida.writeUTF(palabraOculta);
                        letrasErroneas.add(letra);
                        flujo_salida.writeUTF("Letras erroneas: " + letrasErroneas);
                        flujo_salida.writeInt(intentos);
                                            
                    }
                    
                }else if(opcion.equals("palabra")){
                    
                    flujo_salida.writeUTF(palabraOculta);
                    flujo_salida.writeUTF("Introduce la palabra: ");
                    String palabraCliente = flujo_entrada.readUTF();
                    palabraCliente = palabraCliente.toUpperCase();
                    System.out.println("El cliente ha elegido: " + palabraCliente);
                    
                    // si la palabra coincide mostramos acierto sino descontamos fallo y segimos
                    if(palabraCliente.equals(palabraElegida)){
                                
                        flujo_salida.writeUTF(palabraElegida);
                        flujo_salida.writeUTF("Has acertado. FELICIDADES");
                                
                    }else{
                                
                        flujo_salida.writeUTF("Has fallado. Sigue intentandolo");
                        flujo_salida.writeUTF(palabraOculta);
                                
                    }
                    
                }   
                
            }
            
        }catch (Exception e) {
            
            // Manejar excepciones
            System.out.println(e.getMessage());
            
        }
    }
    
    public static String crearPalabraAleatoria(String[] nivelElegido){
        
        Random r = new Random();
        int numeroAleatorio = r.nextInt(10);
        
        String palabraAleatoria = nivelElegido[numeroAleatorio];
        
        return palabraAleatoria;
        
    }
    
    public static String crearPalabraOculta(String palabraAleatoria){
        
        int longitudPalabra = palabraAleatoria.length();
        String[] palabraOculta = new String[longitudPalabra];
        String palabraFinal;
        
        for(int i=0; i<palabraOculta.length; i++){
            palabraOculta[i] = "_";
        }
        
        palabraFinal = (convertirArrayAString(palabraOculta));
        
        return palabraFinal;
        
    }
    
    public static String convertirArrayAString(String[] array) {
        StringBuilder palabra = new StringBuilder();
        
        for (String letra : array) {
            palabra.append(letra);
        }
        
        return palabra.toString();
    }
    
    public static String palabraIntermedia(String palabraAleatoria, String palabraOculta, String letra, int aciertos) {
        // Convertir la letra a un único carácter en mayúsculas (asumiendo que la letra es una cadena de un solo carácter)
        char letraChar = letra.toUpperCase().charAt(0);

        // Crear un StringBuilder para modificar la palabraOculta de manera eficiente
        StringBuilder palabraOcultaBuilder = new StringBuilder(palabraOculta);

        // Recorrer letra a letra la palabra aleatoria
        for (int i = 0; i < palabraAleatoria.length(); i++) {
            // Verificar si la letra actual es igual a la letra proporcionada
            if (Character.toUpperCase(palabraAleatoria.charAt(i)) == letraChar) {
                // Modificar esa posición en palabraOcultaBuilder
                palabraOcultaBuilder.setCharAt(i, letraChar);
                aciertos++;
            }
        }

        // Convertir StringBuilder de vuelta a String
        String palabraOcultaActualizada = palabraOcultaBuilder.toString();
        palabraOculta = palabraOcultaActualizada;

        return palabraOculta;
    }
 
    public static boolean letraEsta(String palabraAleatoria, String letra) {
         
        boolean fin = false;

        // Convertir la letra a un único carácter en mayúsculas (asumiendo que la letra es una cadena de un solo carácter)
        char letraChar = letra.toUpperCase().charAt(0);

        // Recorrer letra a letra la palabra aleatoria
        for (int i = 0; i < palabraAleatoria.length(); i++) {
            // Verificar si la letra actual es igual a la letra proporcionada
            if (Character.toUpperCase(palabraAleatoria.charAt(i)) == letraChar) {
                fin = true;
            }
        }

        return fin;
    }
    
}




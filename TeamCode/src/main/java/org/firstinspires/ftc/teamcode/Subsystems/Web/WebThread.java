package org.firstinspires.ftc.teamcode.Subsystems.Web;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.firstinspires.ftc.robotcore.internal.collections.SimpleGson;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.Drive;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.PIDCoefficients;
import org.firstinspires.ftc.teamcode.Subsystems.Web.Canvas.WebCanvas;
import org.firstinspires.ftc.teamcode.Subsystems.Web.Server.Request;
import org.firstinspires.ftc.teamcode.Subsystems.Web.Server.Response;
import org.firstinspires.ftc.teamcode.Subsystems.Web.Server.WebError;

import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class WebThread extends Thread {

    private static final ArrayList<WebLog> logs = new ArrayList<>();
    private static final ArrayList<WebAction> actions = new ArrayList<>();
    private static final HashMap<String, String> defaultHeaders = new HashMap<>();
    int port;
    ServerSocket serverSocket;
    WebCanvas webCanvas;
    private final Gson gson;
    public static volatile boolean terminate = false;

    public WebThread() throws IOException {
        this(7070);
    }

    public WebThread(int port) throws IOException {
        gson = SimpleGson.getInstance();
        this.port = port;
        this.webCanvas = new WebCanvas(500, 500);
        serverSocket = new ServerSocket(port);
        defaultHeaders.put("Content-Type", "application/json");
        defaultHeaders.put("Server", "Web Subsystem Thread");
    }

    public static void addLog(WebLog log) {
        logs.add(log);
    }

    public static void setPercentage(String task, int percentage) {
        actions.stream().filter(action -> Objects.equals(action.name, task)).findFirst().ifPresent(action -> action.progress = percentage);
    }

    public static void setPercentage(String task, int progress, int total) {
        setPercentage(task, (Math.abs(progress) / Math.abs(total)) * 100);
    }

    public static void addAction(WebAction action) {
        actions.add(action);
    }

    public static void removeAction(String task) {
        actions.removeIf(action -> Objects.equals(action.name, task));
    }

    private static String readHeaders(InputStreamReader reader) throws IOException {
        StringBuilder str = new StringBuilder();
        boolean exit = false;
        int prev = 0;
        while (!exit) {
            int result = reader.read();
            if (result == -1) {
                exit = true;
            } else if (result == 13 && prev == 10) {
                exit = true;
            } else {
                str.append((char) result);
            }
            prev = result;
        }
        return str.toString();
    }

    private Response returnError(WebError error) {
        return new Response(error.statusCode, "ERR", defaultHeaders, gson.toJson(error.toHashMap()));
    }

    private void invalidMethod(String method) throws WebError {
        throw new WebError("Method '" + method + "' not allowed", 405, 4050);
    }

    private Response returnObject(Object obj) {
        return new Response(200, "OK", defaultHeaders, gson.toJson(obj));
    }

    private Response handleRequest(Request req) throws WebError {
        if (Objects.equals(req.url, "/")) {
            if (Objects.equals(req.method, "GET")) {
                return returnObject(new View.MainResponse(logs, actions, Drive.currentPose));
            } else {
                invalidMethod(req.method);
            }
        } else if (Objects.equals(req.url, "/logs")) {
            if (Objects.equals(req.method, "GET")) {
                return returnObject(logs);
            } else {
                invalidMethod(req.method);
            }
        } else if (Objects.equals(req.url, "/actions")) {
            if (Objects.equals(req.method, "GET")) {
                return returnObject(actions);
            } else {
                invalidMethod(req.method);
            }
        } else if (Objects.equals(req.url, "/position")) {
            if (Objects.equals(req.method, "GET")) {
                return returnObject(Drive.currentPose);
            } else {
                invalidMethod(req.method);
            }
        } else if (Objects.equals(req.url, "/gamepads")) {
            if (Objects.equals(req.method, "GET")) {
                return returnObject(new View.GamepadResponse(Robot.gamepad1, Robot.gamepad2));
            } else {
                invalidMethod(req.method);
            }
        } else if (Objects.equals(req.url, "/drive")) {
            if (Objects.equals(req.method, "GET")) {
                return returnObject(new View.DriveResponse());
            } else if (Objects.equals(req.method, "PATCH")) {
                Type type = new TypeToken<Map<String, String>>() {
                }.getType();
                Map<String, String> request = gson.fromJson(req.data, type);
                Drive.xyPIDCoefficients = new PIDCoefficients(
                        Double.parseDouble(request.getOrDefault("xyP", String.valueOf(Drive.xyPIDCoefficients.kP))),
                        Double.parseDouble(request.getOrDefault("xyI", String.valueOf(Drive.xyPIDCoefficients.kI))),
                        Double.parseDouble(request.getOrDefault("xyD", String.valueOf(Drive.xyPIDCoefficients.kD)))
                );
                Drive.thetaPIDCoefficients = new PIDCoefficients(
                        Double.parseDouble(request.getOrDefault("thetaP", String.valueOf(Drive.thetaPIDCoefficients.kP))),
                        Double.parseDouble(request.getOrDefault("thetaI", String.valueOf(Drive.thetaPIDCoefficients.kI))),
                        Double.parseDouble(request.getOrDefault("thetaD", String.valueOf(Drive.thetaPIDCoefficients.kD)))
                );
                return returnObject(new View.DriveResponse());
            } else if (Objects.equals(req.method, "/move")) {
                if (Objects.equals(req.method, "POST")) {
                    invalidMethod(req.method); // TODO: implement
                } else {
                    invalidMethod(req.method);
                }
            } else {
                invalidMethod(req.method);
            }
        } else if (Objects.equals(req.url, "/canvas")) {
            if (Objects.equals(req.method, "GET")) {
                HashMap<String, String> headers = new HashMap<>(2);
                headers.put("Server", "Web Subsystem Thread");
                headers.put("Content-Type", "image/unknown");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                BufferedOutputStream bw = new BufferedOutputStream(stream);
                try {
                    bw.write(244);
                    bw.write(322);
                } catch (IOException e) {
                    Log.e("WebThread", "Error writing to stream: " + e.getMessage(), e);
                }
                return new Response(200, "OK", headers, stream);
            } else {
                invalidMethod(req.method);
            }
        }
        throw new WebError("Resource not found", 404, 4040);
    }

    /**
     * <p>Workflow:
     * <p>- Read socket to end
     * <p>- Parse request ({@link Request#Request(String)})
     * <p>- Generate response ({@link WebThread#handleRequest(Request)})
     * <p>- Return response
     */
    @Override
    public void run() {
        while (!terminate) {
            try {
                Socket socket = serverSocket.accept();
                InputStreamReader reader = new InputStreamReader(socket.getInputStream());
                String headers = readHeaders(reader);
                try {
                    Request req = new Request(headers); // TODO: Fix tech debt
                    for (int i = 0; i <= Integer.parseInt(req.headers.getOrDefault("Content-Length", "0")); i++) {
                        req.data += (char) reader.read();
                    }
                    System.out.println(req.method + " " + req.url + " " + socket.getInetAddress().getHostAddress());
                    Response resp = handleRequest(req);
                    OutputStream output = socket.getOutputStream();
                    output.write(resp.toBytes());
                    output.close();
                } catch (WebError e) {
                    OutputStream output = socket.getOutputStream();
                    Response resp = returnError(e);
                    output.write(resp.toBytes());
                    output.close();
                } catch (Exception e) {
                    OutputStream output = socket.getOutputStream();
                    Response resp = returnError(new WebError(e.getMessage(), 500, 5000));
                    output.write(resp.toBytes());
                    output.close();
                    System.out.println("Unhandled Error on WebThread, graceful exit performed: " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("Unhandled Error on WebThread, connection terminated without response: " + e.getMessage());
            }
        }
    }
}

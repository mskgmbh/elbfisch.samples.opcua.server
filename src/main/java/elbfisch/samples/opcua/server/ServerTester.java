/**
 * PROJECT   : Elbfisch - java process automation controller (jPac)
 * MODULE    : ServerTester.java
 * VERSION   : -
 * DATE      : -
 * PURPOSE   : 
 * AUTHOR    : Bernd Schuster, MSK Gesellschaft fuer Automatisierung mbH, Schenefeld
 * REMARKS   : -
 * CHANGES   : CH#n <Kuerzel> <datum> <Beschreibung>
 *
 * This file is part of the jPac process automation controller.
 * jPac is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * jPac is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the jPac If not, see <http://www.gnu.org/licenses/>.
 */

package elbfisch.samples.opcua.server;

import java.util.concurrent.Executors;
import static javafx.application.Application.launch;
import javafx.stage.Stage;
import org.jpac.Module;
import org.jpac.fx.DashboardLauncher;
import org.jpac.fx.test.ModuleTesterTemplate;

/**
 *
 * @author berndschuster
 */
public class ServerTester extends ModuleTesterTemplate{
    /**
     * @param args the command line arguments
     */
    static boolean done = false;
    static int     cmd  = 0;
    
    public static void main(String[] args) {
        Module module = new ServerMain();
        module.start();
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.submit(() -> {
//            System.out.println("Hello world");
//            //Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
//            SignedInteger command = (SignedInteger) SignalRegistry.getInstance().getSignal("Main.lastCommand");
//            do{
//                command.setDeferred(cmd++);
//                try{Thread.sleep(20);}catch(InterruptedException exc){};
//            }
//            while(!done);
//        });        
        launch(args);
        done = true;
    }
    
    @Override
    public void start(Stage primaryStage) {
        try{
            DashboardLauncher dashboardLauncher = new DashboardLauncher();
            dashboardLauncher.show();
        }
        catch(Exception exc){
            exc.printStackTrace();
        }
    }
}

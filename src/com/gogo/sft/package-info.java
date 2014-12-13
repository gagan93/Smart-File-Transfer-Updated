/**
 * Smart File Transfer is a utility which can be used to transfer files across
 * LAN (computers connected to the same network). Basically, the idea of
 * creating such an application came to my mind when my java end semester exam
 * was approaching. Moreover, I had just purchased a new laptop and wanted an
 * easy method of tranferring regular files between the two. I googled and got
 * an open source utility HFS (HTTP File Server). After that I studies streams
 * and sockets and started creating this application. It is not as good as those
 * available in the market (even open source) but at least its mine :).
 * 
 * The above stuff was for advanced people. For beginners, you can learn various
 * concepts like use of event dispatching thread, a bit of multithreading
 * (including safe way to stop threads) and a very important class (SwingWorker)
 * which you may need for creating applications involving fast updation of GUI
 * with respect to some manipulation / calculation. For eg. updating a Progress
 * bar.
 * 
 * I created this application when I was a java beginner so the software design
 * is not that good (entire client part in one class, entire server part in one
 * class, not good documentation and many more issues). So please spare me for
 * that.
 * 
 * I would love to hear both criticism and appreciations.
 * 
 * @author Gagandeep Singh
 * 
 *         For any suggesstions/bugs/improvements , you can mail me @
 *         gagan_93@live.com Facebook profile : www.fb.com/gagan93.
 * 
 * 
 *         Classes Information :-
 * 
 *         1. Client : Client class contains entire GUI + Logic for Client (file
 *         reciever).
 * 
 *         2. Menu : The class which allows you to choose between client and
 *         server (or what you want to do).This is the main class
 * 
 *         3. MyJButton : A class extending JButton (javax.swing.JButton) and
 *         overriding certain GUI properties for my own ease.
 * 
 *         4. MyJLabel :A class extending JLabel (javax.swing.JLabel) and
 *         overriding certain GUI properties for my own ease.
 * 
 *         5. Server : Server class contains entire GUI + Logic for Server (file
 *         sender).
 * 
 */
package com.gogo.sft;
package test.mvc;

import java.awt.Font;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mvc.Controller;
import mvc.Model;
import mvc.View;

public class TestMVCApp {

    private static final String OUTPUT_FILE = "MVC_data.csv";
    static final int NUMBER_OF_VIEWS = 100;
    static final int NUMBER_OF_SPINNER = NUMBER_OF_VIEWS;
    static final int DIVIDER = 4;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        Files.deleteIfExists(Path.of(OUTPUT_FILE));

        Files.writeString(Path.of(OUTPUT_FILE),
                "iter,view_total,view_num,spin_total,spin_num,time,memory" + System.lineSeparator(),
                StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        System.out.println("iter,view_total,view_num,spin_total,spin_num,time,memory");

        for (int i = 1; i <= 12; i++) {
            performMeasurement(i);
        }

        long endTime = System.currentTimeMillis();
        long totalTimeInMillis = endTime - startTime;
        double totalTimeInMinutes = totalTimeInMillis / 1000.0 / 60.0; // Convert milliseconds to minutes
        System.out.println("Total Time: " + totalTimeInMinutes + " minutes");
    }

    private static void performMeasurement(int iteration) throws IOException {

        List<Integer> viewTotalList = new ArrayList<>();
        viewTotalList.add(1);
        for (int multiplier1 = 1; multiplier1 <= DIVIDER; multiplier1++) {
            int val = (int) (NUMBER_OF_VIEWS / DIVIDER * multiplier1);
            viewTotalList.add(val);
        }

        for (int viewTotal : viewTotalList) {

            List<Integer> spinnerTotalList = new ArrayList<>();
            spinnerTotalList.add(1);
            for (int multiplier2 = 1; multiplier2 <= DIVIDER; multiplier2++) {
                int val = (int) (NUMBER_OF_SPINNER / DIVIDER * multiplier2);
                spinnerTotalList.add(val);
            }

            List<View> viewList = new ArrayList<>();

            for (int spinnerTotal : spinnerTotalList) {

                viewList.clear();
                System.gc();

                for (int viewNumber = 1; viewNumber <= viewTotal; viewNumber++) {
                    Model model = new Model(String.valueOf(((NUMBER_OF_VIEWS * 10) + viewNumber)));
                    View view = new View();
                    view.setName(String.valueOf(viewNumber));
                    view.setTitle("View" + ((NUMBER_OF_VIEWS * 10) + viewNumber));
                    view.getContentPane().removeAll();
                    view.setDefaultCloseOperation(View.DISPOSE_ON_CLOSE);
                    viewList.add(view);

                    model.setView(view);
                    Controller.getModels().put(model.getName(), model);

                    for (int spinnerNumber = 0; spinnerNumber < spinnerTotal; spinnerNumber++) {

                        JSpinner inputSpinner = new JSpinner();
                        inputSpinner.setName("spinner" + ((viewTotal * 10) + spinnerNumber));
                        ((JSpinner.DefaultEditor) inputSpinner.getEditor()).getTextField().setColumns(3);
                        inputSpinner.setFont(new Font("Dialog", Font.BOLD, 32));
                        view.getContentPane().add(inputSpinner);

                        JSpinner outputSpinner = new JSpinner();
                        outputSpinner.setEnabled(false);
                        outputSpinner.setName("spinner" + ((viewTotal * 10) + spinnerNumber) + "b");
                        ((JSpinner.DefaultEditor) outputSpinner.getEditor()).getTextField().setColumns(3);
                        outputSpinner.setFont(new Font("Dialog", Font.BOLD, 32));
                        view.getContentPane().add(outputSpinner);

                        inputSpinner.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                int value = (int) inputSpinner.getValue();
                                Controller.handleChange(model.getName(), inputSpinner.getName(), value);
                            }
                        });
                    }

                    view.setVisible(true);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (View view : viewList) {
                    view.dispose();
                }
                
                String outputString = constructOutputString(iteration, viewTotal, spinnerTotal);
                if (viewTotal > 1 && spinnerTotal > 1) {
                    Files.writeString(Path.of(OUTPUT_FILE), outputString, StandardCharsets.UTF_8,
                            StandardOpenOption.APPEND);
                    System.out.print(outputString);
                }
            }
        }
    }

    private static String constructOutputString(int iteration, int viewTotal, int spinnerTotal) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < spinnerTotal; i++) {
            output.append(String.format("%d,%d,%d,%d,%d,%d,%d%n", iteration, viewTotal, viewTotal, spinnerTotal, i,
                    System.currentTimeMillis(), Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        }
        return output.toString();
    }
}

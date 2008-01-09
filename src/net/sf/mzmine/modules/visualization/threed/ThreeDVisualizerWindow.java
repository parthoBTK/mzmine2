/*
 * Copyright 2006 The MZmine Development Team
 * 
 * This file is part of MZmine.
 * 
 * MZmine is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.visualization.threed;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

import net.sf.mzmine.io.RawDataFile;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.taskcontrol.Task;
import net.sf.mzmine.taskcontrol.TaskListener;
import net.sf.mzmine.taskcontrol.Task.TaskPriority;
import net.sf.mzmine.taskcontrol.Task.TaskStatus;
import net.sf.mzmine.userinterface.Desktop;
import net.sf.mzmine.util.CursorPosition;
import visad.ConstantMap;
import visad.DataReference;
import visad.DisplayImpl;
import visad.ProjectionControl;
import visad.bom.PickManipulationRendererJ3D;
import visad.java3d.MouseBehaviorJ3D;

/**
 * 3D visualizer using VisAd library
 */
public class ThreeDVisualizerWindow extends JInternalFrame implements
         TaskListener, MouseWheelListener, ActionListener {

    private ThreeDToolBar toolBar;
    private JLabel titleLabel;

    private RawDataFile dataFile;
    private RawDataFile rawDataFile;
    private int msLevel;

    private DisplayImpl display;

    // reference to a peak data
    private DataReference peaksDataReference;
    private boolean peaksShown = false;
    private ConstantMap[] peakColorMap;
    private PickManipulationRendererJ3D peakRenderer;

    private Desktop desktop;

    public ThreeDVisualizerWindow(RawDataFile dataFile, int msLevel,
            float rtMin, float rtMax, float mzMin, float mzMax,
            int rtResolution, int mzResolution) {

        super(dataFile.toString(), true, true, true, true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBackground(Color.white);

        this.desktop = MZmineCore.getDesktop();
        this.dataFile = dataFile;
        this.rawDataFile = dataFile;
        this.msLevel = msLevel;

        toolBar = new ThreeDToolBar(this);
        add(toolBar, BorderLayout.EAST);

        titleLabel = new JLabel();
        titleLabel.setFont(titleLabel.getFont().deriveFont(10f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        int scanNumbers[] = rawDataFile.getScanNumbers(msLevel, rtMin, rtMax);
        if (scanNumbers.length == 0) {
            desktop.displayErrorMessage("No scans found at MS level " + msLevel
                    + " within given retention time range.");
            return;
        }

        Task updateTask = new ThreeDSamplingTask(dataFile, scanNumbers, rtMin,
                rtMax, mzMin, mzMax, rtResolution, mzResolution, this);

        MZmineCore.getTaskController().addTask(updateTask, TaskPriority.HIGH,
                this);

    }

    /**
     * @see net.sf.mzmine.modules.RawDataVisualizer#setMZRange(double, double)
     */
    public void setMZRange(double mzMin, double mzMax) {
        // do nothing
    }

    /**
     * @see net.sf.mzmine.modules.RawDataVisualizer#setRTRange(double, double)
     */
    public void setRTRange(double rtMin, double rtMax) {
        // do nothing

    }

    /**
     * @see net.sf.mzmine.modules.RawDataVisualizer#setIntensityRange(double,
     *      double)
     */
    public void setIntensityRange(double intensityMin, double intensityMax) {
        // do nothing
    }

    void updateTitle() {

        StringBuffer title = new StringBuffer();

        title.append(dataFile.toString());
        title.append(": 3D view");

        setTitle(title.toString());

        title.append(", MS");
        title.append(msLevel);

        titleLabel.setText(title.toString());

    }

    /**
     * @param peakRenderer
     * @param peaksDataReference The peaksDataReference to set.
     * @param peakColorMap
     */
    void setPeaksDataReference(PickManipulationRendererJ3D peakRenderer,
            DataReference peaksDataReference, ConstantMap[] peakColorMap) {
        this.peakRenderer = peakRenderer;
        this.peaksDataReference = peaksDataReference;
        this.peakColorMap = peakColorMap;
        this.peaksShown = true;
    }

    /**
     * @see net.sf.mzmine.modules.RawDataVisualizer#getCursorPosition()
     */
    public CursorPosition getCursorPosition() {
        return null;
    }

    /**
     * @see net.sf.mzmine.modules.RawDataVisualizer#setCursorPosition(net.sf.mzmine.util.CursorPosition)
     */
    public void setCursorPosition(CursorPosition newPosition) {
        // do nothing
    }

    /**
     * @see net.sf.mzmine.taskcontrol.TaskListener#taskFinished(net.sf.mzmine.taskcontrol.Task)
     */
    public void taskFinished(Task task) {

        if (task.getStatus() == TaskStatus.ERROR) {
            desktop.displayErrorMessage("Error while updating 3D visualizer: "
                    + task.getErrorMessage());
            return;
        }

        if (task.getStatus() == TaskStatus.FINISHED) {

            // add the 3D component
            display = ((ThreeDSamplingTask) task).getResult();
            Component threeDPlot = display.getComponent();
            threeDPlot.setPreferredSize(new Dimension(700, 500));
            threeDPlot.addMouseWheelListener(this);
            add(threeDPlot, BorderLayout.CENTER);
            updateTitle();
            pack();

            desktop.addInternalFrame(this);
        }

    }

    /**
     * @see net.sf.mzmine.taskcontrol.TaskListener#taskStarted(net.sf.mzmine.taskcontrol.Task)
     */
    public void taskStarted(Task task) {
        // do nothing
    }

    /**
     * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
     */
    public void mouseWheelMoved(MouseWheelEvent event) {

        int rot = event.getWheelRotation();
        try {

            ProjectionControl pControl = display.getProjectionControl();
            double[] pControlMatrix = pControl.getMatrix();

            // scale depending on wheel rotation direction
            double scale = (rot < 0 ? 1.03 : 0.97);

            double[] mult = MouseBehaviorJ3D.static_make_matrix(0.0, 0.0, 0.0,
                    scale, 0.0, 0.0, 0.0);

            double newMatrix[] = MouseBehaviorJ3D.static_multiply_matrix(mult,
                    pControlMatrix);

            pControl.setMatrix(newMatrix);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event) {

        String command = event.getActionCommand();

        if (command.equals("PROPERTIES")) {
            ThreeDPropertiesDialog dialog = new ThreeDPropertiesDialog(desktop,
                    display);
            dialog.setVisible(true);
        }

        if (command.equals("SHOW_ANNOTATIONS")) {

            if (peaksDataReference == null)
                return;

            try {

                if (peaksShown) {
                    display.removeReference(peaksDataReference);
                    peaksShown = false;
                } else {
                    display.addReferences(peakRenderer, peaksDataReference,
                            peakColorMap);
                    peaksShown = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
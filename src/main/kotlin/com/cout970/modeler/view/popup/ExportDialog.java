package com.cout970.modeler.view.popup;

import com.cout970.modeler.export.ExportFormat;
import com.cout970.modeler.export.ExportProperties;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import javax.swing.*;

/**
 * Created by cout970 on 2017/01/02.
 */
public class ExportDialog {
    private JButton exportButton;
    private JButton cancelButton;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JButton selectButton;
    private JPanel root;
    private JTextField yourModIDTextField;
    private JTextField materialsTextField;

    private String defaultFileName = "model.obj";

    @SuppressWarnings("unchecked")
    public static void show(Function1<ExportProperties, Unit> returnFun) {
        JDialog frame = new JDialog();
        ExportDialog dialog = new ExportDialog();
        dialog.comboBox1.addItem("Obj (*.obj)");
        dialog.comboBox1.addItem("Mcx (*.mcx)");
        //buttons
        dialog.cancelButton.addActionListener(e -> {
            returnFun.invoke(null);
            frame.setVisible(false);
        });
        dialog.exportButton.addActionListener(e -> {
            ExportProperties prop = new ExportProperties(
                    dialog.textField1.getText(),
                    ExportFormat.values()[dialog.comboBox1.getSelectedIndex()],
                    dialog.materialsTextField.getText(),
                    dialog.yourModIDTextField.getText()
            );
            frame.setVisible(false);
            returnFun.invoke(prop);
        });
        dialog.selectButton.addActionListener(e -> {
            frame.toBack();
            String file = TinyFileDialogs.tinyfd_saveFileDialog("Export", dialog.defaultFileName,
                    PopupsKt.getExportFileExtensions(ExportFormat.values()[dialog.comboBox1.getSelectedIndex()]),
                    (String) dialog.comboBox1.getSelectedItem());
            dialog.textField1.setText(file);
            frame.toFront();
        });
        dialog.comboBox1.addActionListener(e -> {
            ExportFormat format = ExportFormat.values()[dialog.comboBox1.getSelectedIndex()];
            if (format == ExportFormat.OBJ) {
                dialog.defaultFileName = "model.obj";
                dialog.yourModIDTextField.setEnabled(false);
                dialog.materialsTextField.setEnabled(true);
            } else {
                dialog.defaultFileName = "model.mcx";
                dialog.yourModIDTextField.setEnabled(true);
                dialog.materialsTextField.setEnabled(false);
            }
        });

        dialog.yourModIDTextField.setEnabled(false);
        frame.setContentPane(dialog.root);
        frame.setIconImage(PopupsKt.getPopupImage());
        frame.pack();
        frame.setTitle("Export");
        PopupsKt.center(frame);
        frame.setVisible(true);
        frame.toFront();
    }
}

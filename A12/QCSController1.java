//package qcs;

import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

/**
 * The `QCSController1` class serves as the main controller and view for the Quantum Circuit Simulator (QCS) application.
 * It extends `JFrame` to provide a graphical user interface and implements `ActionListener` to handle user interactions.
 * This class manages the layout, components, and event handling for designing and simulating quantum circuits.
 */
public class QCSController1 extends JFrame implements ActionListener {
    /**
     * The default number of qubits (rows) for the quantum circuit.
     */
    int defaultWidth = 3;
    /**
     * The default depth (columns) for the quantum circuit.
     */
    int defaultLength = 5;
    /**
     * `ResourceBundle` for managing localized messages and text within the application.
     */
    private ResourceBundle messages;
    /**
     * The current `Locale` used for internationalization.
     */
    private Locale currentLocale;
    
    // New instance variables for enhanced functionality
    /**
     * A 2D array of `JButton` objects representing the grid of quantum gates in the circuit.
     */
    private JButton[][] circuitGrid;
    /**
     * Stores the name of the currently selected quantum gate from the gates panel.
     */
    private String selectedGate = "";
    /**
     * A boolean flag indicating whether the application is in design mode (true) or play/simulation mode (false).
     */
    private boolean designMode = true;
    /**
     * Tracks the current step in the circuit execution when in play mode.
     */
    private int currentStep = 0;
    /**
     * Defines the maximum number of steps allowed for circuit execution.
     */
    private int maxSteps = 5;
    /**
     * `JLabel` to display the current step count during circuit execution.
     */
    private JLabel stepCountLabel;
    /**
     * `JTextArea` to display messages, logs, and user feedback.
     */
    private JTextArea messagesArea;
    /**
     * `JScrollPane` for the messages area, enabling scrolling for long messages.
     */
    private JScrollPane messagesScrollPane;
    /**
     * `JPanel` that holds the visual representation of the quantum circuit grid.
     */
    private JPanel quantumCircuitPanel;
    /**
     * A 2D array of strings storing the configuration of gates placed on the circuit.
     */
    private String[][] circuitConfiguration;
    
    // Menu items that need to be updated when language changes
    /**
     * `JMenuItem` for creating a new circuit.
     */
    private JMenuItem newItem;
    /**
     * `JMenuItem` for opening an existing circuit.
     */
    private JMenuItem openItem;
    /**
     * `JMenuItem` for saving the current circuit.
     */
    private JMenuItem saveItem;
    /**
     * `JMenuItem` for exiting the application.
     */
    private JMenuItem exitItem;
    /**
     * `JMenu` for file-related operations (New, Open, Save, Exit).
     */
    private JMenu fileMenu;
    /**
     * `JMenu` for application settings (Dark Mode, Change Language, Change Look & Feel).
     */
    private JMenu settingsMenu;
    /**
     * `JMenu` for help-related options (About).
     */
    private JMenu helpMenu;
    /**
     * `JMenuItem` for displaying information about the application.
     */
    private JMenuItem aboutItem;
    /**
     * `JMenuItem` for changing the application's language.
     */
    private JMenuItem changeLanguageItem;
    /**
     * `JMenuItem` for changing the application's look and feel.
     */
    private JMenuItem changeLookFeelItem;
    /**
     * `JCheckBoxMenuItem` for toggling dark mode on and off.
     */
    private JCheckBoxMenuItem darkModeToggle;

    /**
     * Constructs a new `QCSController1` object.
     * Initializes the main JFrame, sets up the default locale and resource bundle for internationalization,
     * initializes the circuit configuration, and creates the various UI components.
     */
    QCSController1() {
        // Set locale and resource bundle with proper path
        // Attempts to load the resource bundle for the current locale.
        // It tries two paths: "qcs.QCSMessages" (with package prefix) and "QCSMessages" (without).
        // If both attempts fail, it sets `messages` to null, and the application will use default English text.
        currentLocale = new Locale("en");
        try {
            messages = ResourceBundle.getBundle("qcs.QCSMessages", currentLocale);
        } catch (Exception e) {
            try {
                // Try without package prefix
                messages = ResourceBundle.getBundle("QCSMessages", currentLocale);
            } catch (Exception e2) {
                // If resource bundle is not found, we'll use default English text
                messages = null;
                System.out.println("Resource bundle not found, using default text");
            }
        }
        
        // Initialize circuit configuration
        // Creates a new 2D array to store the gate configuration,
        // initializing each cell to an empty string, indicating no gate is present.
        circuitConfiguration = new String[defaultWidth][defaultLength];
        for (int i = 0; i < defaultWidth; i++) {
            for (int j = 0; j < defaultLength; j++) {
                circuitConfiguration[i][j] = "";
            }
        }

        // Sets the size, title, default close operation, resizable property, background color, and layout of the main JFrame.
        this.setSize(420, 420);
        this.setTitle("Quantum Circuit Simulator - Emmanuella/Irvanah ");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.getContentPane().setBackground(new Color(235, 235, 235));
        this.setLayout(new BorderLayout(10, 10));

        // Calls methods to create the menu bar and the main panels of the application.
        createMenuBar();
        createMainPanels();
        
        // Adds an initial message to the messages area indicating the application has started in Design Mode.
        addMessage("Application started in Design Mode");
        // Makes the main JFrame visible to the user.
        this.setVisible(true);
    }

    /**
     * Creates the menu bar for the application, including File, Settings, and Help menus.
     * Each menu contains various items that trigger specific actions.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        // Initializes the "File" menu and its items (New, Open, Save, Exit).
        // Each item is assigned an action command and an action listener.
        fileMenu = new JMenu(getMessage("File"));
        newItem = new JMenuItem(getMessage("New"));
        newItem.setActionCommand("NEW");
        newItem.addActionListener(this);
        openItem = new JMenuItem(getMessage("Open"));
        openItem.setActionCommand("OPEN");
        openItem.addActionListener(this);
        saveItem = new JMenuItem(getMessage("Save"));
        saveItem.setActionCommand("SAVE");
        saveItem.addActionListener(this);
        exitItem = new JMenuItem(getMessage("Exit"));
        exitItem.setActionCommand("EXIT");
        exitItem.addActionListener(e -> System.exit(0)); // Directly exits the application

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator(); // Adds a separator line
        fileMenu.add(exitItem);

        // Settings Menu
        // Initializes the "Settings" menu and its items (Dark Mode, Change Language, Change Look & Feel).
        // The Dark Mode toggle has an `ItemListener` to immediately apply theme changes.
        settingsMenu = new JMenu(getMessage("Settings"));
        darkModeToggle = new JCheckBoxMenuItem(getMessage("DarkMode"));
        darkModeToggle.setActionCommand("DARK_MODE");
        darkModeToggle.addItemListener(e -> {
            boolean dark = darkModeToggle.isSelected();
            getContentPane().setBackground(dark ? Color.DARK_GRAY : new Color(235, 235, 235));
            addMessage(getMessage(dark ? "DarkModeEnabled" : "DarkModeDisabled"));
        });
        
        changeLanguageItem = new JMenuItem(getMessage("ChangeLanguage"));
        changeLanguageItem.setActionCommand("CHANGE_LANGUAGE");
        changeLanguageItem.addActionListener(this);
        
        changeLookFeelItem = new JMenuItem(getMessage("ChangeLookFeel"));
        changeLookFeelItem.setActionCommand("CHANGE_LOOK_FEEL");
        changeLookFeelItem.addActionListener(this);
        
        settingsMenu.add(darkModeToggle);
        settingsMenu.add(changeLanguageItem);
        settingsMenu.add(changeLookFeelItem);

        // Help Menu
        // Initializes the "Help" menu and its "About" item.
        helpMenu = new JMenu(getMessage("Help"));
        aboutItem = new JMenuItem(getMessage("About"));
        aboutItem.setActionCommand("ABOUT");
        aboutItem.addActionListener(this);
        helpMenu.add(aboutItem);

        // Adds all created menus to the menu bar.
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);
        menuBar.add(helpMenu);
        // Sets the created menu bar as the JFrame's menu bar.
        this.setJMenuBar(menuBar);
    }

    /**
     * Creates and arranges the main panels of the application:
     * - A header panel with a logo.
     * - A left panel for quantum gates.
     * - A center panel for the quantum circuit grid.
     * - A bottom panel for controls and messages.
     */
    private void createMainPanels() {
        // Header with logo (using a placeholder if image not found)
        // Attempts to load an image for the logo. If the image is not found, a placeholder text label is used instead.
        JLabel logo;
        try {
            logo = new JLabel(new ImageIcon("qcs.jpg"), JLabel.CENTER);
        } catch (Exception e) {
            logo = new JLabel("QCS Logo", JLabel.CENTER);
            logo.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            logo.setPreferredSize(new Dimension(100, 50));
        }
        
        // Adds a mouse listener to the logo to display the "About" dialog when clicked.
        logo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showAbout();
            }
        });
        // Creates a panel to hold the logo and adds it to the top (NORTH) of the frame.
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.add(logo);
        add(headerPanel, BorderLayout.NORTH);

        // Left panel - Quantum Gates
        // Calls the method to create the panel containing the quantum gates.
        createQuantumGatesPanel();
        
        // Center panel - Quantum Circuit
        // Calls the method to create the panel representing the quantum circuit grid.
        createQuantumCircuitPanel();
        
        // Bottom panel - Controls and Messages
        // Calls the method to create the panel for controls and messages.
        createBottomPanel();
    }

    /**
     * Creates the panel containing buttons for various quantum gates,
     * categorized into Single Qubit, Multi Qubit, Operations, and Phase Parameters.
     * This panel is placed on the left side (WEST) of the main frame.
     */
    private void createQuantumGatesPanel() {
        JPanel quantumGatesPanel = new JPanel();
        quantumGatesPanel.setLayout(new BoxLayout(quantumGatesPanel, BoxLayout.Y_AXIS));
        // Sets a titled border for the main quantum gates panel.
        quantumGatesPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                getMessage("QuantumGates"),
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.BLACK
        ));

        // Single Qubit Gates
        // Creates a panel for single-qubit gates and adds buttons for each gate type.
        // Each button has a distinct background color and an action listener.
        JPanel singleQubit = new JPanel(new GridLayout(4, 2, 5, 5));
        String[] singleGateNames = {"I", "X", "Y", "Z", "H", "S", "T", "U"};
        Color[] singleColors = {Color.YELLOW, Color.BLUE, Color.RED, Color.PINK, 
                                Color.PINK, Color.PINK, Color.PINK, Color.PINK};

        for (int i = 0; i < singleGateNames.length; i++) {
            JButton btn = new JButton(singleGateNames[i]);
            btn.setBackground(singleColors[i]);
            btn.setActionCommand("GATE_" + singleGateNames[i]);
            btn.addActionListener(this);
            singleQubit.add(btn);
        }

        // Sets a titled border for the single-qubit gates sub-panel.
        singleQubit.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                getMessage("SingleQubit"),
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.BLACK
        ));
        quantumGatesPanel.add(singleQubit);

        // Multi Qubit Gates
        // Creates a panel for multi-qubit gates and adds buttons for each gate type.
        // Each button has a distinct background color and an action listener.
        JPanel multiQubit = new JPanel(new GridLayout(4, 1, 5, 5));
        String[] multiLabels = {"CX", "SWAP", "CU", "CCX"};
        Color[] multiColors = {Color.YELLOW, Color.GREEN, Color.RED, Color.PINK};
        for (int i = 0; i < multiLabels.length; i++) {
            JButton btn = new JButton(multiLabels[i]);
            btn.setBackground(multiColors[i]);
            btn.setActionCommand("GATE_" + multiLabels[i]);
            btn.addActionListener(this);
            multiQubit.add(btn);
        }
        // Sets a titled border for the multi-qubit gates sub-panel.
        multiQubit.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                getMessage("MultiQubit"),
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.BLACK
        ));
        quantumGatesPanel.add(multiQubit);

        // Operations
        // Creates a panel for operations and adds a button for the "BARRIER" operation.
        JPanel operations = new JPanel(new GridLayout(1, 1, 5, 5));
        JButton barrier = new JButton("BARRIER");
        barrier.setBackground(Color.GRAY);
        barrier.setActionCommand("GATE_BARRIER");
        barrier.addActionListener(this);
        operations.add(barrier);
        // Sets a titled border for the operations sub-panel.
        operations.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                getMessage("Operations"),
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.BLACK
        ));
        quantumGatesPanel.add(operations);

        // Phase Parameters
        // Creates a panel for phase parameters, including text fields for 'a', 'b', and 'c' values.
        JPanel phase = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField textArea1 = new JTextField("0.0");
        JTextField textArea2 = new JTextField("0.0");
        JTextField textArea3 = new JTextField("0.0");
        phase.add(new JLabel("a:")); phase.add(textArea1);
        phase.add(new JLabel("b:")); phase.add(textArea2);
        phase.add(new JLabel("c:")); phase.add(textArea3);
        // Sets a titled border for the phase parameters sub-panel.
        phase.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                getMessage("PhaseParameters"),
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.BLACK
        ));
        quantumGatesPanel.add(phase);
        
        // Adds the main quantum gates panel to the left (WEST) region of the frame.
        add(quantumGatesPanel, BorderLayout.WEST);
    }

    /**
     * Creates and initializes the quantum circuit panel, which displays the grid of quantum gates.
     * This panel uses a `GridLayout` to arrange `JButton` components, each representing a potential gate position.
     * This panel is placed in the center (CENTER) of the main frame.
     */
    private void createQuantumCircuitPanel() {
        // Initializes the main circuit panel with a grid layout based on default width and length.
        quantumCircuitPanel = new JPanel(new GridLayout(defaultWidth, defaultLength, 1, 1));
        // Initializes the 2D array of JButtons that will represent the circuit grid.
        circuitGrid = new JButton[defaultWidth][defaultLength];
        
        // Populates the circuit grid with JButtons.
        for (int i = 0; i < defaultWidth; i++) {
            for (int j = 0; j < defaultLength; j++) {
                JButton gate = new JButton();
                gate.setBackground(Color.WHITE); // Sets default background color for empty cells
                gate.setActionCommand("CIRCUIT_" + i + "_" + j); // Sets a unique action command for each cell
                gate.addActionListener(this); // Adds action listener to handle clicks on circuit cells
                circuitGrid[i][j] = gate; // Stores the button in the grid array
                quantumCircuitPanel.add(gate); // Adds the button to the panel
            }
        }
        
        // Sets a titled border for the quantum circuit panel.
        quantumCircuitPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                getMessage("QuantumCircuit"),
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.BLACK
        ));
        // Adds the quantum circuit panel to the center (CENTER) region of the frame.
        add(quantumCircuitPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the bottom panel of the application, which includes control options,
     * a tensor product display, and a scrollable message area.
     * This panel is placed at the bottom (SOUTH) of the main frame.
     */
    private void createBottomPanel() {
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS)); // Uses BoxLayout for vertical arrangement

        // Control Options - First
        // Creates a panel for control buttons such as New Circuit, Step, Reset, and Mode Toggle.
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel options = new JPanel(new GridLayout(0, 5, 3, 3)); // Grid layout for buttons
        
        // Initializes the step count label.
        stepCountLabel = new JLabel("Step:" + currentStep + "/" + maxSteps);

        // Creates and adds the "New Circuit" button.
        JButton newCircuit = new JButton(getMessage("NewCircuit"));
        newCircuit.setActionCommand("NEW_CIRCUIT");
        newCircuit.addActionListener(this);
        options.add(newCircuit);

        // Creates and adds the "Step" button.
        JButton step = new JButton(getMessage("Step"));
        step.setActionCommand("STEP");
        step.addActionListener(this);
        options.add(step);

        // Creates and adds the "Reset" button.
        JButton reset = new JButton(getMessage("Reset"));
        reset.setActionCommand("RESET");
        reset.addActionListener(this);
        options.add(reset);
        
        // Creates and adds the "Mode Toggle" button (Play Mode/Design Mode).
        JButton modeToggle = new JButton(getMessage("PlayMode"));
        modeToggle.setActionCommand("TOGGLE_MODE");
        modeToggle.addActionListener(this);
        options.add(modeToggle);

        // Adds the step count label to the options panel.
        options.add(stepCountLabel);
        optionsPanel.add(options);

        // Tensor Product Display - Second (above messages)
        // Creates a panel to display the tensor product (placeholder).
        JPanel tensor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Tensor Product:|0...0>");
        tensor.add(label);
        tensor.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Messages Area with Scrolling - Last
        // Initializes the text area for messages, makes it non-editable, and enables word wrapping.
        messagesArea = new JTextArea(5, 30);
        messagesArea.setEditable(false);
        messagesArea.setLineWrap(true);
        messagesArea.setWrapStyleWord(true);
        
        // Configures the caret to always auto-scroll to the bottom when new text is added.
        DefaultCaret caret = (DefaultCaret) messagesArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        // Creates a scroll pane for the messages area and sets its border and preferred size.
        messagesScrollPane = new JScrollPane(messagesArea);
        messagesScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                getMessage("Messages"),
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.BLACK
        ));
        messagesScrollPane.setPreferredSize(new Dimension(400, 100));

        // Add components in the correct order to the south panel.
        southPanel.add(optionsPanel);      // First: Control buttons
        southPanel.add(tensor);            // Second: Tensor Product
        southPanel.add(messagesScrollPane); // Third: Messages

        // Adds the complete south panel to the bottom (SOUTH) region of the frame.
        add(southPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Prompts the user to enter new dimensions (number of qubits and circuit depth) for the quantum circuit.
     * If valid positive integers are entered, the circuit is resized and refreshed.
     * Displays error messages for invalid input or if the user cancels.
     */
    private void promptCircuitSize() {
		try {
			// Prompts for the number of qubits (rows).
			String qubitsStr = JOptionPane.showInputDialog(this, "Enter number of qubits (rows):", defaultWidth);
			if (qubitsStr == null) return; // User cancelled
			int newWidth = Integer.parseInt(qubitsStr);

			// Prompts for the circuit depth (columns).
			String depthStr = JOptionPane.showInputDialog(this, "Enter circuit depth (columns):", defaultLength);
			if (depthStr == null) return; // User cancelled
			int newLength = Integer.parseInt(depthStr);

			// Validates that the entered dimensions are positive.
			if (newWidth <= 0 || newLength <= 0) {
				JOptionPane.showMessageDialog(this, "Please enter positive integers.");
				return;
			}

			// Updates the default width and length with the new values.
			this.defaultWidth = newWidth;
			this.defaultLength = newLength;

			// Refreshes the quantum circuit panel to reflect the new size.
			refreshQuantumCircuitPanel();
			// Adds a message to the message area confirming the circuit resize.
			addMessage("Circuit resized to " + newWidth + " qubits and depth " + newLength);

		} catch (NumberFormatException e) {
			// Catches and handles `NumberFormatException` if the user enters non-integer input.
			JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid integers.");
		}
	}
	
	/**
	 * Refreshes the quantum circuit panel by removing the existing one and recreating it with the updated dimensions.
	 * This method is called when the circuit size needs to be dynamically changed.
	 */
	private void refreshQuantumCircuitPanel() {
	    // Gets the current layout manager of the content pane.
	    BorderLayout layout = (BorderLayout) getContentPane().getLayout();
	    // Retrieves the component currently in the CENTER position.
	    Component center = layout.getLayoutComponent(BorderLayout.CENTER);
	    if (center != null) {
	        // If a component exists in the CENTER, remove it.
	        getContentPane().remove(center);
	    }

	    // Add the updated quantum circuit panel
	    // Calls the method to create a new quantum circuit panel with the (potentially) updated defaultWidth and defaultLength.
	    createQuantumCircuitPanel();

	    // Refresh the frame so changes are visible
	    // Revalidates and repaints the frame to ensure that the new circuit panel is displayed correctly.
	    revalidate();
	    repaint();
	}

    /**
     * Handles action events triggered by various UI components.
     * This is the central event dispatcher for button clicks and menu item selections.
     *
     * @param e The `ActionEvent` object containing information about the event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand(); // Gets the action command associated with the event.
        
        // Checks if the command indicates a gate selection.
        if (command.startsWith("GATE_")) {
            handleGateSelection(command.substring(5)); // Extracts gate name and calls handler.
        } 
        // Checks if the command indicates a click on a circuit grid cell.
        else if (command.startsWith("CIRCUIT_")) {
            handleCircuitClick(command); // Calls handler for circuit cell clicks.
        } 
        // Handles other specific action commands from menu items or control buttons.
        else {
            switch (command) {
                case "NEW": // New menu item in File menu
                case "NEW_CIRCUIT": // New Circuit button in control panel
                    promptCircuitSize();
                    break;
                case "SAVE": // Save menu item in File menu
                    saveCircuit();
                    break;
                case "OPEN": // Open menu item in File menu
                    openCircuit();
                    break;
                case "STEP": // Step button in control panel
                    executeStep();
                    break;
                case "RESET": // Reset button in control panel
                    resetCircuit();
                    break;
                case "TOGGLE_MODE": // Toggle Mode button in control panel
                    toggleMode();
                    break;
                case "CHANGE_LANGUAGE": // Change Language menu item in Settings menu
                    changeLanguage();
                    break;
                case "CHANGE_LOOK_FEEL": // Change Look & Feel menu item in Settings menu
                    changeLookAndFeel();
                    break;
                case "ABOUT": // About menu item in Help menu
                    showAbout();
                    break;
            }
        }
    }

    /**
     * Handles the selection of a quantum gate from the quantum gates panel.
     * Updates the `selectedGate` variable and provides feedback in the messages area.
     * Gate selection is only allowed in design mode.
     *
     * @param gateName The name of the selected gate (e.g., "X", "H", "CX").
     */
    private void handleGateSelection(String gateName) {
        if (designMode) { // Checks if the application is in design mode.
            selectedGate = gateName; // Sets the selected gate.
            addMessage(getMessage("GateSelected") + " " + gateName); // Informs the user about the selection.
        } else {
            addMessage(getMessage("CannotSelectGate")); // Warns the user if trying to select a gate in play mode.
        }
    }

    /**
     * Handles clicks on the quantum circuit grid cells.
     * In design mode, it attempts to place the `selectedGate` at the clicked position.
     * In play mode, it provides feedback about the clicked position.
     *
     * @param command The action command string from the clicked circuit button (e.g., "CIRCUIT_0_0").
     */
    private void handleCircuitClick(String command) {
        String[] parts = command.split("_"); // Splits the command string to extract row and column.
        int row = Integer.parseInt(parts[1]); // Parses the row index.
        int col = Integer.parseInt(parts[2]); // Parses the column index.
        
        // Checks if in design mode and a gate has been selected.
        if (designMode && !selectedGate.isEmpty()) {
            placeGate(row, col); // Attempts to place the selected gate.
        } 
        // If in play mode.
        else if (!designMode) {
            addMessage(getMessage("CircuitPosition") + " [" + row + "," + col + "] - " + getMessage("PlayModeActive")); // Provides feedback.
        } 
        // If in design mode but no gate is selected.
        else {
            addMessage(getMessage("SelectGateFirst")); // Prompts the user to select a gate first.
        }
    }

    /**
     * Places the `selectedGate` onto the quantum circuit grid at the specified row and column.
     * A gate can only be placed if the target position is currently empty.
     * After successful placement, the `selectedGate` is cleared.
     *
     * @param row The row index where the gate is to be placed.
     * @param col The column index where the gate is to be placed.
     */
    private void placeGate(int row, int col) {
        // Checks if the target position in the circuit configuration is empty.
        if (circuitConfiguration[row][col].isEmpty()) {
            circuitConfiguration[row][col] = selectedGate; // Updates the circuit configuration with the selected gate.
            circuitGrid[row][col].setText(selectedGate); // Sets the button's text to the gate name.
            circuitGrid[row][col].setBackground(getGateColor(selectedGate)); // Sets the button's background color based on the gate type.
            addMessage(getMessage("GatePlaced") + " " + selectedGate + " " + getMessage("AtPosition") + " [" + row + "," + col + "]"); // Logs the action.
            selectedGate = ""; // Clears the selected gate after placement.
        } else {
            addMessage(getMessage("PositionOccupied") + " [" + row + "," + col + "]"); // Informs the user if the position is already occupied.
        }
    }

    /**
     * Returns the appropriate `Color` for a given quantum gate type.
     *
     * @param gate The name of the quantum gate (e.g., "X", "H", "CX").
     * @return The `Color` associated with the gate, or `Color.PINK` for unknown gates.
     */
    private Color getGateColor(String gate) {
        switch (gate) {
            case "I": return Color.YELLOW;
            case "X": return Color.BLUE;
            case "Y": return Color.RED;
            case "Z": return Color.PINK;
            case "H": return Color.PINK;
            case "CX": return Color.YELLOW;
            case "SWAP": return Color.GREEN;
            case "BARRIER": return Color.GRAY;
            default: return Color.PINK; // Default color for unlisted or new gates.
        }
    }

    /**
     * Resets the quantum circuit to an empty state.
     * Clears all placed gates from the `circuitConfiguration` and the UI `circuitGrid`.
     * Resets the `currentStep` counter and updates the `stepCountLabel`.
     */
    private void newCircuit() {
        // Iterates through all cells of the circuit grid.
        for (int i = 0; i < defaultWidth; i++) {
            for (int j = 0; j < defaultLength; j++) {
                circuitConfiguration[i][j] = ""; // Clears the gate configuration for the cell.
                circuitGrid[i][j].setText(""); // Clears the text on the button.
                circuitGrid[i][j].setBackground(Color.WHITE); // Resets the background color to white.
            }
        }
        currentStep = 0; // Resets the current step to 0.
        stepCountLabel.setText("Step:" + currentStep + "/" + maxSteps); // Updates the step count label.
        addMessage(getMessage("NewCircuitCreated")); // Logs that a new circuit has been created.
    }

    /**
     * Executes one step of the quantum circuit simulation.
     * This action is only permitted in play mode and if the maximum number of steps has not been reached.
     * Increments the `currentStep` and updates the `stepCountLabel`.
     * Provides feedback in the messages area.
     */
    private void executeStep() {
        // Checks if the application is in play mode and if the maximum steps have not been reached.
        if (!designMode && currentStep < maxSteps) {
            currentStep++; // Increments the current step.
            stepCountLabel.setText("Step:" + currentStep + "/" + maxSteps); // Updates the step count label.
            addMessage(getMessage("StepExecuted") + " " + currentStep); // Logs the executed step.
        } 
        // If in design mode, advises the user to switch to play mode.
        else if (designMode) {
            addMessage(getMessage("SwitchToPlayMode"));
        } 
        // If maximum steps have been reached in play mode.
        else {
            addMessage(getMessage("MaxStepsReached"));
        }
    }

    /**
     * Resets the quantum circuit's simulation state to its initial conditions.
     * Sets `currentStep` back to 0 and updates the `stepCountLabel`.
     * Does not clear the placed gates on the circuit grid.
     */
    private void resetCircuit() {
        currentStep = 0; // Resets the current step.
        stepCountLabel.setText("Step:" + currentStep + "/" + maxSteps); // Updates the step count label.
        addMessage(getMessage("CircuitReset")); // Logs that the circuit has been reset.
    }

    /**
     * Toggles the application's mode between Design Mode and Play Mode.
     * Updates the `designMode` flag and the text of the mode toggle button.
     * Provides feedback in the messages area about the mode change.
     */
    private void toggleMode() {
        designMode = !designMode; // Flips the boolean value of designMode.
        updateModeButton(); // Calls a helper method to update the text of the mode toggle button.
        addMessage(getMessage("SwitchedTo") + " " + (designMode ? getMessage("DesignMode") : getMessage("PlayMode"))); // Logs the mode change.
    }

    /**
     * Updates the text of the mode toggle button to reflect the current mode
     * (e.g., "Play Mode" if currently in Design Mode, or "Design Mode" if currently in Play Mode).
     * This method traverses the component hierarchy to find the specific button.
     */
    private void updateModeButton() {
        // Initiates a recursive search for the mode toggle button starting from the content pane.
        findAndUpdateModeButton(getContentPane());
    }

    /**
     * Recursively searches for and updates the text of the "TOGGLE_MODE" button within a given container.
     *
     * @param container The `Container` to search within.
     */
    private void findAndUpdateModeButton(Container container) {
        // Iterates through all components within the current container.
        for (Component comp : container.getComponents()) {
            // If the component is a JButton.
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                // If the button's action command matches "TOGGLE_MODE".
                if ("TOGGLE_MODE".equals(btn.getActionCommand())) {
                    // Updates the button's text based on the current designMode.
                    btn.setText(designMode ? getMessage("PlayMode") : getMessage("DesignMode"));
                    return; // Found and updated, so exit the recursion.
                }
            } 
            // If the component is another container, recursively call the method.
            else if (comp instanceof Container) {
                findAndUpdateModeButton((Container) comp);
            }
        }
    }

    /**
     * Simulates saving the current circuit configuration.
     * Currently, it only adds a message to the messages area and shows a confirmation dialog.
     */
    private void saveCircuit() {
        addMessage(getMessage("CircuitSaved")); // Logs the save action.
        JOptionPane.showMessageDialog(this, getMessage("CircuitSavedMessage")); // Shows a confirmation message to the user.
    }

    /**
     * Simulates opening a circuit configuration.
     * Currently, it only adds a message to the messages area and shows an informational dialog.
     */
    private void openCircuit() {
        addMessage(getMessage("LoadingCircuit")); // Logs the loading action.
        JOptionPane.showMessageDialog(this, getMessage("OpenCircuitMessage")); // Informs the user that the functionality is pending.
    }

    /**
     * Changes the application's language between English ("en") and French ("fr").
     * Reloads the `ResourceBundle` for the new locale and then updates all UI components.
     * Provides feedback in the messages area about the language change.
     */
    private void changeLanguage() {
        // Toggles the current locale between English and French.
        if (currentLocale.getLanguage().equals("en")) {
            currentLocale = new Locale("fr");
            addMessage("Langue changée en français"); // Adds a French message for the change.
        } else {
            currentLocale = new Locale("en");
            addMessage("Language changed to English"); // Adds an English message for the change.
        }
        
        // Try to load new resource bundle
        // Attempts to load the resource bundle for the newly set locale.
        // It tries two paths: "qcs.QCSMessages" (with package prefix) and "QCSMessages" (without).
        // If both attempts fail, it logs a message.
        try {
            messages = ResourceBundle.getBundle("qcs.QCSMessages", currentLocale);
        } catch (Exception ex) {
            try {
                // Try without package prefix
                messages = ResourceBundle.getBundle("QCSMessages", currentLocale);
            } catch (Exception e2) {
                addMessage("Language resource not found, using default text");
            }
        }
        
        // Update all UI components with new language
        // Calls a helper method to update all visible UI elements with the new language's text.
        updateUILanguage();
    }
    
    /**
     * Updates all user interface components (menu items, border titles, control buttons)
     * with the text from the currently loaded `ResourceBundle` (based on the `currentLocale`).
     * This method ensures that the UI reflects the chosen language.
     */
    private void updateUILanguage() {
        // Update menu items
        // Sets the text for all menu items and menus.
        fileMenu.setText(getMessage("File"));
        newItem.setText(getMessage("New"));
        openItem.setText(getMessage("Open"));
        saveItem.setText(getMessage("Save"));
        exitItem.setText(getMessage("Exit"));
        
        settingsMenu.setText(getMessage("Settings"));
        darkModeToggle.setText(getMessage("DarkMode"));
        changeLanguageItem.setText(getMessage("ChangeLanguage"));
        changeLookFeelItem.setText(getMessage("ChangeLookFeel"));
        
        helpMenu.setText(getMessage("Help"));
        aboutItem.setText(getMessage("About"));
        
        // Update borders
        // Calls a recursive method to update the titles of all TitledBorders within the content pane.
        updateBorderTitles(getContentPane());
        
        // Update buttons in the control panel
        // Calls a recursive method to update the text of specific control buttons.
        updateControlButtons();
        
        // Refresh the UI
        // Informs Swing to update the entire component tree to reflect the changes.
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    /**
     * Recursively traverses the component hierarchy to find and update the titles of `TitledBorder` instances.
     * This is used to localize the titles of panels.
     *
     * @param container The `Container` to search within.
     */
    private void updateBorderTitles(Container container) {
        // Iterates through all components within the current container.
        for (Component comp : container.getComponents()) {
            // Checks if the component is a JComponent (which can have a border).
            if (comp instanceof JComponent) {
                JComponent jcomp = (JComponent) comp;
                // Checks if the component's border is a TitledBorder.
                if (jcomp.getBorder() instanceof TitledBorder) {
                    TitledBorder border = (TitledBorder) jcomp.getBorder();
                    String title = border.getTitle(); // Gets the current title of the border.
                    
                    // Updates specific border titles based on their current (potentially English or French) value.
                    if (title.equals("Quantum Gates") || title.equals("Portes Quantiques")) {
                        border.setTitle(getMessage("QuantumGates"));
                    } else if (title.equals("Single Qubit") || title.equals("Qubit Unique")) {
                        border.setTitle(getMessage("SingleQubit"));
                    } else if (title.equals("Multi Qubit") || title.equals("Multi Qubit")) { // Note: French translation for "Multi Qubit" missing in default messages.
                        border.setTitle(getMessage("MultiQubit"));
                    } else if (title.equals("Operations") || title.equals("Opérations")) {
                        border.setTitle(getMessage("Operations"));
                    } else if (title.equals("Phase Parameters") || title.equals("Paramètres de Phase")) {
                        border.setTitle(getMessage("PhaseParameters"));
                    } else if (title.equals("Quantum Circuit") || title.equals("Circuit Quantique")) {
                        border.setTitle(getMessage("QuantumCircuit"));
                    } else if (title.equals("Messages") || title.equals("Messages")) {
                        border.setTitle(getMessage("Messages"));
                    }
                }
            }
            
            // If the component is another container, recursively call the method.
            if (comp instanceof Container) {
                updateBorderTitles((Container) comp);
            }
        }
    }
    
    /**
     * Updates the text of specific control buttons within the application's UI.
     * This method traverses the component hierarchy to find and update buttons
     * identified by their action commands.
     */
    private void updateControlButtons() {
        // Starts the recursive search for control buttons from the content pane.
        findAndUpdateControlButtons(getContentPane());
    }
    
    /**
     * Recursively searches for and updates the text of specific control buttons
     * within a given container based on their action commands.
     *
     * @param container The `Container` to search within.
     */
    private void findAndUpdateControlButtons(Container container) {
        // Iterates through all components in the current container.
        for (Component comp : container.getComponents()) {
            // Checks if the component is a JButton.
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String actionCommand = btn.getActionCommand(); // Gets the action command of the button.
                
                // Updates the button's text based on its action command and the current localized messages.
                if ("NEW_CIRCUIT".equals(actionCommand)) {
                    btn.setText(getMessage("NewCircuit"));
                } else if ("STEP".equals(actionCommand)) {
                    btn.setText(getMessage("Step"));
                } else if ("RESET".equals(actionCommand)) {
                    btn.setText(getMessage("Reset"));
                } else if ("TOGGLE_MODE".equals(actionCommand)) {
                    // Special handling for the mode toggle button as its text depends on the current mode.
                    btn.setText(designMode ? getMessage("PlayMode") : getMessage("DesignMode"));
                }
            }
            
            // If the component is another container, recursively call the method.
            if (comp instanceof Container) {
                findAndUpdateControlButtons((Container) comp);
            }
        }
    }

    /**
     * Allows the user to change the Look and Feel (L&F) of the Swing application.
     * Presents a dialog with available L&F options and applies the selected one.
     * Provides feedback in the messages area.
     */
    private void changeLookAndFeel() {
        try {
            // Retrieves all installed Look and Feel information.
            UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
            String[] lookNames = new String[looks.length];
            // Populates an array with the names of available L&F options.
            for (int i = 0; i < looks.length; i++) {
                lookNames[i] = looks[i].getName();
            }
            
            // Displays an input dialog for the user to choose a Look and Feel.
            String selected = (String) JOptionPane.showInputDialog(
                this,
                getMessage("ChooseLookAndFeel"),
                getMessage("LookAndFeel"),
                JOptionPane.QUESTION_MESSAGE,
                null, // No icon
                lookNames, // Array of choices
                lookNames[0] // Default selected value
            );
            
            // If a selection was made (user didn't cancel).
            if (selected != null) {
                // Iterates through the installed L&F options to find the selected one.
                for (UIManager.LookAndFeelInfo look : looks) {
                    if (look.getName().equals(selected)) {
                        // Sets the new Look and Feel.
                        UIManager.setLookAndFeel(look.getClassName());
                        // Updates the entire Swing component tree to apply the new L&F.
                        SwingUtilities.updateComponentTreeUI(this);
                        addMessage(getMessage("LookAndFeelChanged") + " " + selected); // Logs the change.
                        break; // Exit loop once applied.
                    }
                }
            }
        } catch (Exception ex) {
            // Catches and logs any errors that occur during the L&F change.
            addMessage(getMessage("ErrorChangingLookAndFeel") + " " + ex.getMessage());
        }
    }

    /**
     * Displays an "About" dialog box providing information about the Quantum Circuit Simulator project.
     * Includes project name, version, team, course details, and year.
     * An optional icon (`qcsicon.jpg`) is displayed if found.
     */
    private void showAbout() {
        // Attempts to load an image for the about dialog icon.
        ImageIcon icon = new ImageIcon("qcsicon.jpg");
        // Displays the about dialog with localized messages.
        JOptionPane.showMessageDialog(this, 
                getMessage("QCS Project") + "\n" +
                getMessage("Version") + " 1.0\n" +
                getMessage("Team") + " I.F/E.R\n" +
                getMessage("Course") + " CST8221 - JAP\n" +
                getMessage("Year") + " - 2025",
                getMessage("About") + " QCS",
                JOptionPane.INFORMATION_MESSAGE,
                icon);
        
    }

    /**
     * Appends a new message to the `messagesArea` and ensures the text area automatically scrolls to the bottom.
     *
     * @param message The string message to be added.
     */
    private void addMessage(String message) {
        // Checks if the messagesArea JTextArea has been initialized.
        if (messagesArea != null) {
            messagesArea.append(message + "\n"); // Appends the message followed by a new line.
            // Scrolls to the very end of the document, making the new message visible.
            messagesArea.setCaretPosition(messagesArea.getDocument().getLength());
        }
    }

    /**
     * Retrieves a localized string message based on a given key from the `ResourceBundle`.
     * If the `ResourceBundle` is not available or the key is not found, it falls back to a default English message.
     *
     * @param key The key corresponding to the desired message in the resource bundle.
     * @return The localized message string, or a default English message if localization fails.
     */
    private String getMessage(String key) {
        // Checks if the messages ResourceBundle is loaded.
        if (messages != null) {
            try {
                return messages.getString(key); // Returns the localized string for the given key.
            } catch (Exception e) {
                // If the key is not found in the loaded bundle, fall back to default.
                return getDefaultMessage(key);
            }
        } else {
            // If the ResourceBundle itself is not loaded, directly use default messages.
            return getDefaultMessage(key);
        }
    }

    /**
     * Provides default English messages for various UI elements and application messages.
     * This serves as a fallback when the `ResourceBundle` is not found or a specific key is missing.
     *
     * @param key The key for which to retrieve the default message.
     * @return The default English message string for the given key.
     */
    private String getDefaultMessage(String key) {
        // Fallback messages when resource bundle is not available
        switch (key) {
            // Menu items
            case "File": return "File";
            case "New": return "New";
            case "Open": return "Open";
            case "Save": return "Save";
            case "Exit": return "Exit";
            case "Settings": return "Settings";
            case "DarkMode": return "Dark Mode";
            case "Help": return "Help";
            case "About": return "About";
            case "ChangeLanguage": return "Change Language";
            case "ChangeLookFeel": return "Change Look & Feel";
            
            // Panel titles
            case "QuantumGates": return "Quantum Gates";
            case "SingleQubit": return "Single Qubit";
            case "MultiQubit": return "Multi Qubit";
            case "Operations": return "Operations";
            case "PhaseParameters": return "Phase Parameters";
            case "QuantumCircuit": return "Quantum Circuit";
            case "Messages": return "Messages";
            
            // Buttons
            case "NewCircuit": return "New Circuit";
            case "Step": return "Step";
            case "Reset": return "Reset";
            case "PlayMode": return "Play Mode";
            case "DesignMode": return "Design Mode";
            
            // Messages
            case "GateSelected": return "Gate selected:";
            case "CannotSelectGate": return "Cannot select gates in Play mode";
            case "CircuitPosition": return "Circuit position";
            case "PlayModeActive": return "Play mode active";
            case "SelectGateFirst": return "Select a gate first";
            case "GatePlaced": return "Gate";
            case "AtPosition": return "placed at position";
            case "PositionOccupied": return "Position already occupied:";
            case "NewCircuitCreated": return "New circuit created";
            case "StepExecuted": return "Step executed:";
            case "SwitchToPlayMode": return "Switch to Play mode to execute steps";
            case "MaxStepsReached": return "Maximum steps reached";
            case "CircuitReset": return "Circuit reset to initial state";
            case "SwitchedTo": return "Switched to";
            case "CircuitSaved": return "Circuit configuration saved to memory";
            case "CircuitSavedMessage": return"Circuit saved successfully!";
            case "LoadingCircuit": return "Loading circuit configuration...";
            case "OpenCircuitMessage": return "Open circuit functionality - to be implemented";
            case "DarkModeEnabled": return "Dark mode enabled";
            case "DarkModeDisabled": return "Dark mode disabled";
            
            // Look and Feel
            case "ChooseLookAndFeel": return "Choose Look and Feel:";
            case "LookAndFeel": return "Look and Feel";
            case "LookAndFeelChanged": return "Look and Feel changed to:";
            case "ErrorChangingLookAndFeel": return "Error changing Look and Feel:";
            
            // About dialog
            case "AboutTitle": return "Quantum Circuit Simulator";
            case "Version": return "Version";
            case "Team": return "Team:";
            case "Course": return "Course:";
            case "College": return "Algonquin College";
            
            default: return key; // If no default message is found, return the key itself.
        }
    }

    /**
     * The main entry point for the Quantum Circuit Simulator application.
     * It uses `SwingUtilities.invokeLater` to ensure that the GUI is created and updated
     * on the Event Dispatch Thread (EDT), which is crucial for Swing applications.
     *
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QCSController1()); // Creates and runs the QCSController1 on the EDT.
    }
}
package net.synchthia.misq.gate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.NonNull;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public class GateStore {
    private static Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(Gate.class, new GateAdapter())
            .create();

    private final GatePlugin plugin;
    private final File file;
    private Map<String, Gate> gates = new HashMap<>();

    public GateStore(GatePlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getPlugin().getDataFolder(), "gates.json");

        if (file.exists()) {
            try {
                load();
            } catch (IOException e) {
                plugin.getPlugin().getLogger().log(Level.SEVERE, "Failed load data", e);
            }
        }
    }

    private void load() throws IOException {
        Gate[] loadedGates;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            loadedGates = gson.fromJson(br, Gate[].class);
        }

        gates.clear();

        for (Gate gate : loadedGates) {
            if (gate.getName() == null) {
                continue;
            }
            gates.put(gate.getName(), gate);
        }
    }

    public void save() throws IOException {
        String json = gson.toJson(gates.values());

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(json);
            fw.flush();
        }
    }

    public boolean add(@NonNull Gate gate) {
        Optional<Gate> result = gates.values().stream()
                .filter(v -> gate.getGateArea() == v.getGateArea() || gate.getName().equals(v.getName()))
                .findFirst();

        if (result.isPresent()) {
            // Already Defined
            return false;
        }

        gates.put(gate.getName(), gate);
        return true;
    }

    public boolean remove(@NonNull String key) {
        if (gates.containsKey(key)) {
            gates.remove(key);
            return true;
        }
        return false;
    }

    public Map<String, Gate> get() {
        return this.gates;
    }
}

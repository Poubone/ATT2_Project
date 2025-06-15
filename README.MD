# 🎮 ATT2 Mod for Minecraft 1.20.2 (Fabric) – by Poubone

> A Minecraft mod that enhances gameplay for the *Across The Time 2* adventure map with immersive HUDs, intuitive menus, and gameplay integrations.

---

## ✨ Features

- 🎯 **Custom HUD** – Displays **Mana**, **XP**, **Chronotons**, and **Arrows**
- 🛠️ **Radial Menus** – For **storage** and **item repair**
- 📈 **Stat Upgrade System** – With custom UI and command integration
- 📦 **Direct support** for **ATT2 datapack** features via in-game functions
- 📡 **Item Hover Broadcast** – Share your held item in chat with hoverable tooltip

> 💬 *Currently available in **French only***  
> 🌍 Translations & localization updates are planned.

---

## 📡 Item Hover System

Players can broadcast the item in their hand to nearby players.  
When the key is pressed (default: **O**), the item is **temporarily shared** via an invisible entity and displayed in chat with a hoverable tooltip.

### ⚙️ How it works

- Pressing the key triggers a `function` that:
    - stores the held item,
    - spawns an invisible armor stand holding that item,
    - removes it after a few seconds.
- All clients detect these broadcasts and render a **hoverable chat message** for the item.

### 📦 Requirements

> This system **requires the official datapack** to be installed and loaded.

Make sure the datapack (`mymod_broadcast_delayed_setitem.zip`) is in your world’s `datapacks/` folder and correctly activated.

You can download it directly from the [Releases](../../releases) tab.

---

## 🧪 Compilation

> Make sure you have the following:

- ☕ Java 17+
- 🛠️ Gradle
- 🧱 Minecraft 1.20.2 (Fabric modloader)

```bash
./gradlew build
```

The compiled `.jar` file will appear in the `build/libs` folder.

---

## 🧩 Installation

1. Download the `.jar` file from the [Releases](../../releases) tab.
2. Drop it into your Minecraft `mods/` folder (Fabric loader required).
3. Launch the game!

---

## 🛠 First-Time Setup

After joining your world for the first time:
- Go to the keybind settings,
- Toggle the HUD off and back on to ensure scoreboard objectives initialize correctly.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

🛑 **Note:** This mod is made specifically for the **Across The Time 2** map. Using it outside of this context may not function properly.

🔗 [Download the latest release](https://github.com/Poubone/ATT2_Project/releases/latest)
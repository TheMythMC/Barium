package dev.themyth.barium.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.render.MessageRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.awt.*;

public class ModGui extends Screen implements IMessageConsumer {

    private final Screen parent;
    protected ModGui(Text title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    public void init() {
        this.addDrawableChild(new ButtonWidget(this.width/2, this.height/2, 20, 120, new LiteralText("Update"), button -> {
            // include all the logic here so we can make new messages
        }));
    }

    @Override
    public void addMessage(Message.MessageType type, String messageKey, Object... args) {
        this.addMessage(type, 5000, messageKey, args);
    }

    @Override
    public void addMessage(Message.MessageType type, int lifeTime, String messageKey, Object... args) {
        this.addGuiMessage(type, lifeTime, messageKey, args);
    }

    private void addGuiMessage(Message.MessageType type, int lifetime, String messageKey, Object... args) {
        new MessageRenderer(0xDD000000, 0xFF999999).addMessage(type, lifetime, messageKey, args);
    }
}

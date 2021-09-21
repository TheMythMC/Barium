package dev.themyth.barium.gui;

import dev.themyth.barium.Barium;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.render.MessageRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
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
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 130, this.height/4, 120, 20, new LiteralText("Update"), button -> {
            Barium.sendMessage("This is a message!", MinecraftClient.getInstance().player);
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 10, this.height /3, 120, 20, new LiteralText("Ignored Mods"), button -> {
            // i have no idea how i am going to do this.
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 130, this.height - 50, 260, 20, new LiteralText("Done"), (buttonWidget) -> MinecraftClient.getInstance().setScreen(parent)));
    }
    @Override
    public void render(MatrixStack matricies, int x, int y, float delta) {
        this.renderBackground(matricies);
        drawCenteredText(matricies, this.textRenderer, this.title, this.width / 2, 20, 16777215);
        super.render(matricies, x, y, delta);
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

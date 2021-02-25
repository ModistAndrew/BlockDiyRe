package modist.blockdiy.client.gui;

import modist.blockdiy.common.tileentity.DiyBlockTileEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiDiyBlock extends Screen {
	private DiyBlockTileEntity tileentity;
	private TextFieldWidget flagTextField;
	private Button doneBtn;
	private Button cancelBtn;

	public GuiDiyBlock(ITextComponent titleIn) {
		super(titleIn);
	}

	@Override
	public void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.doneBtn = new Button(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20,
				I18n.format("gui.done"), button->this.update());
		this.cancelBtn = new Button(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20,
				I18n.format("gui.cancel"), button->this.onClose());
		this.flagTextField = new TextFieldWidget(this.font, this.width / 2 - 150, 50, 300, 20, "text");
		this.addButton(doneBtn);
		this.addButton(cancelBtn);
		this.children.add(flagTextField);
	}

	@Override
	public void removed() {
		this.minecraft.keyboardListener.enableRepeatEvents(false);
	}

	private void update() {
		// TODO Auto-generated method stub	
		this.onClose();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();
		flagTextField.render(mouseX, mouseY, partialTicks);
		super.render(mouseX, mouseY, partialTicks);
	}

}
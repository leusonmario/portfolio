package name.abuchen.portfolio.ui.handlers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import name.abuchen.portfolio.ui.Messages;
import name.abuchen.portfolio.ui.PortfolioPlugin;
import name.abuchen.portfolio.ui.dialogs.CheckedListSelectionDialog;

import org.eclipse.e4.ui.internal.workbench.PartServiceSaveHandler;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CustomSaveHandler extends PartServiceSaveHandler
{
    private static final class PartLabelProvider extends LabelProvider
    {
        @Override
        public String getText(Object element)
        {
            MPart part = (MPart) element;
            String tooltip = part.getTooltip();
            return tooltip != null ? part.getLabel() + " (" + part.getTooltip() + ")" : part.getLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        }

        @Override
        public Image getImage(Object element)
        {
            return PortfolioPlugin.image(PortfolioPlugin.IMG_LOGO_16);
        }
    }

    private static final class PromptForSaveDialog extends MessageDialog
    {
        private PromptForSaveDialog(Shell parentShell, String dialogMessage)
        {
            super(parentShell, Messages.SaveHandlerTitle, null, dialogMessage, MessageDialog.INFORMATION, //
                            new String[] { Messages.LabelYes, Messages.LabelNo, Messages.LabelCancel }, 0);

            setShellStyle(getShellStyle() | SWT.SHEET);
        }
    }

    @Override
    public Save promptToSave(MPart dirtyPart)
    {
        String prompt = MessageFormat.format(Messages.SaveHandlerPrompt, dirtyPart.getLabel());

        MessageDialog dialog = new PromptForSaveDialog(Display.getDefault().getActiveShell(), prompt);

        switch (dialog.open())
        {
            case 0:
                return Save.YES;
            case 1:
                return Save.NO;
            case 2:
            default:
                return Save.CANCEL;
        }
    }

    @Override
    public Save[] promptToSave(Collection<MPart> dirtyParts)
    {
        if (dirtyParts.size() == 1)
            return new Save[] { promptToSave(dirtyParts.iterator().next()) };
        else
            return promptToSaveMultiple(dirtyParts);
    }

    private Save[] promptToSaveMultiple(Collection<MPart> dirtyParts)
    {
        CheckedListSelectionDialog dialog = new CheckedListSelectionDialog(Display.getDefault().getActiveShell(),
                        new PartLabelProvider());
        dialog.setTitle(Messages.SaveHandlerTitle);
        dialog.setMessage(Messages.SaveHandlerMsgSelectFileToSave);
        dialog.setElements(dirtyParts);

        int returnCode = dialog.open();

        Save[] answer = new Save[dirtyParts.size()];

        if (returnCode == Dialog.OK)
        {
            Arrays.fill(answer, Save.NO);
            if (dialog.getResult() != null)
            {
                List<MPart> parts = new ArrayList<MPart>(dirtyParts);
                for (Object toBeSaved : dialog.getResult())
                    answer[parts.indexOf(toBeSaved)] = Save.YES;
            }
        }
        else if (returnCode == Dialog.CANCEL)
        {
            Arrays.fill(answer, Save.CANCEL);
        }

        return answer;
    }

}

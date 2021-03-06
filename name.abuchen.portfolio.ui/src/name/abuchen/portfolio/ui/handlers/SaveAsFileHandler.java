package name.abuchen.portfolio.ui.handlers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import javax.inject.Named;

import name.abuchen.portfolio.model.ClientFactory;
import name.abuchen.portfolio.ui.Messages;
import name.abuchen.portfolio.ui.PortfolioPart;
import name.abuchen.portfolio.ui.PortfolioPlugin;
import name.abuchen.portfolio.ui.UIConstants;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

public class SaveAsFileHandler
{
    @CanExecute
    boolean isVisible(@Named(IServiceConstants.ACTIVE_PART) MPart part)
    {
        return Platform.OS_LINUX.equals(Platform.getOS())
                        || (null != part && part.getObject() instanceof PortfolioPart);
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part,
                    @Named(IServiceConstants.ACTIVE_SHELL) Shell shell,
                    @Named(UIConstants.Parameter.EXTENSION) String extension,
                    @Named(UIConstants.Parameter.ENCRYPTION_METHOD) @Optional String encryptionMethod)
    {
        if (part == null || !(part.getObject() instanceof PortfolioPart))
        {
            MessageDialog.openWarning(shell, Messages.MsgNoFileOpen, Messages.MsgNoFileOpenText);
            return;
        }

        if (extension == null)
            throw new IllegalArgumentException("Missing file extension parameter"); //$NON-NLS-1$

        // check whether encryption is supported
        if ("AES256".equals(encryptionMethod) && !ClientFactory.isKeyLengthSupported(256)) //$NON-NLS-1$
        {
            new JurisdictionFilesDownloadDialog(shell).open();
            return;
        }

        // trigger part to save file
        ((PortfolioPart) part.getObject()).doSaveAs(part, shell, extension, encryptionMethod);
    }

    private static class JurisdictionFilesDownloadDialog extends MessageDialog
    {
        public JurisdictionFilesDownloadDialog(Shell parentShell)
        {
            super(parentShell, Messages.JurisdictionFilesDownloadTitle, null,
                            Messages.JurisdictionFilesDownloadMessage, CONFIRM,
                            new String[] { IDialogConstants.OK_LABEL }, 0);
        }

        @Override
        protected Control createCustomArea(Composite parent)
        {
            Composite container = new Composite(parent, SWT.NONE);
            GridLayoutFactory.fillDefaults().numColumns(1).applyTo(container);

            // Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction
            // Policy Files Download URL
            String javaVersion = System.getProperty("java.version"); //$NON-NLS-1$
            String downloadURL = null;
            if (javaVersion.startsWith("1.6")) //$NON-NLS-1$
                downloadURL = "http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html"; //$NON-NLS-1$
            else if (javaVersion.startsWith("1.7")) //$NON-NLS-1$
                downloadURL = "http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html"; //$NON-NLS-1$
            else if (javaVersion.startsWith("1.8")) //$NON-NLS-1$
                downloadURL = "http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html"; //$NON-NLS-1$
            else
                downloadURL = "http://www.oracle.com/technetwork/java/javase/downloads/"; //$NON-NLS-1$

            // Java home directory
            File target = new File(System.getProperty("java.home"), "lib/security"); //$NON-NLS-1$ //$NON-NLS-2$

            String message = MessageFormat.format(Messages.JurisdictionFilesDownloadExplanation, downloadURL,
                            target.getAbsolutePath());

            Link explanation = new Link(container, SWT.NONE);
            explanation.setText(message);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(explanation);

            explanation.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent event)
                {
                    if (Desktop.isDesktopSupported())
                    {
                        Desktop desktop = Desktop.getDesktop();
                        if (desktop.isSupported(Desktop.Action.BROWSE))
                        {
                            try
                            {
                                desktop.browse(new URI(String.valueOf(event.text)));
                            }
                            catch (IOException ignore)
                            {
                                PortfolioPlugin.log(ignore);
                            }
                            catch (URISyntaxException ignore)
                            {
                                PortfolioPlugin.log(ignore);
                            }
                        }
                    }
                }
            });

            return explanation;
        }
    }
}

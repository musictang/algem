package org.openconcerto.module.algem.accounting;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;

import org.openconcerto.erp.config.Gestion;
import org.openconcerto.erp.modules.AbstractModule;
import org.openconcerto.erp.modules.MenuContext;
import org.openconcerto.erp.modules.ModuleFactory;
import org.openconcerto.erp.modules.ModuleManager;
import org.openconcerto.erp.modules.ModulePackager;
import org.openconcerto.sql.ui.ConnexionPanel;
import org.openconcerto.ui.FrameUtil;
import org.openconcerto.ui.PanelFrame;
import org.openconcerto.utils.FileUtils;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version
 * @since
 */
public class Module extends AbstractModule {

	public Module(ModuleFactory f) throws IOException {
		super(f);
	}

	protected void setupMenu(MenuContext menuContext) {

		menuContext.addMenuItem(new CustomImportEcritureAction(), "Algem");
	}

	@Override
	protected void start() {

	}

	@Override
	protected void stop() {

	}

	class CustomImportEcritureAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public CustomImportEcritureAction() {
			super("Import d'écritures algem");

		}

		@Override
		public void actionPerformed(ActionEvent e) {

			final PanelFrame frame = new PanelFrame(new CustomImportEcriturePanel(), "Import d'écritures");
			frame.pack();
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);
			FrameUtil.show(frame);

		}
	}
	
	public static void main(String[] args) throws IOException {
        System.setProperty(ConnexionPanel.QUICK_LOGIN, "true");
        final File propsFile = new File("module.properties");

        final File distDir = new File("dist");
        FileUtils.mkdir_p(distDir);
        final ModulePackager modulePackager = new ModulePackager(propsFile, new File("bin/"));
        //modulePackager.addJarsFromDir(new File("lib"));
        modulePackager.writeToDir(distDir);
//        modulePackager.writeToDir(new File("../OpenConcerto/Modules"));
        modulePackager.writeToDir(new File("/opt/OpenConcerto-1.4-multiposte/Modules"));
        //// SQLRequestLog.setEnabled(true);
        ////SQLRequestLog.showFrame();

//        ModuleManager.getInstance().addFactories(new File("../OpenConcerto/Modules"));
        ModuleManager.getInstance().addFactories(new File("/opt/OpenConcerto-1.4-multiposte/Modules"));
        Gestion.main(args);
    }

}

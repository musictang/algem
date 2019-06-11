/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016-2017 Musiques Tangentes. All rights reserved.
 *
 * The contents of this file are subject to the terms of the GNU General Public License Version 3
 * only ("GPL"). You may not use this file except in compliance with the License. You can obtain a
 * copy of the License at http://www.gnu.org/licenses/gpl-3.0.html See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each file.
 */
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
 * @version 1.1 19/09/17
 * @since 1.0 15/12/2016
 */
public class Module extends AbstractModule {

	public Module(ModuleFactory f) throws IOException {
		super(f);
	}

    @Override
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
		String defaultModulesPath = "/opt/OpenConcerto-1.4.2-multiposte/Modules";
		String modulesPath = defaultModulesPath;
		if (args != null && args.length > 0) {
			modulesPath = args[0];
		}
		
        final File propsFile = new File("module.properties");

        final File distDir = new File("dist");
        FileUtils.mkdir_p(distDir);
        final ModulePackager modulePackager = new ModulePackager(propsFile, new File("bin/"));
        //modulePackager.addJarsFromDir(new File("lib"));
        modulePackager.writeToDir(distDir);
        modulePackager.writeToDir(new File(modulesPath));
        //// SQLRequestLog.setEnabled(true);
        ////SQLRequestLog.showFrame();

        ModuleManager.getInstance().addFactories(new File(modulesPath));
        Gestion.main(args);
    }

}

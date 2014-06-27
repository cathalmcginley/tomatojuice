package org.gnostai.tomatojuice.gtkui

import org.gnome.gtk._
import org.gnome.gdk.Pixbuf

class MainMenu extends WidgetManager {

  val menuBar = new MenuBar()
  buildMenuBar

  override protected[gtkui] def widget: Widget = menuBar

  def buildMenuBar {
    buildPomodoroMenu
    buildHelpMenu
  }

  def buildPomodoroMenu {
    // Pomodoro Menu
    val pomodoroItem = new MenuItem("Pomodoro")
    menuBar.append(pomodoroItem)

    // submenu items
    val quitMenu = new Menu()
    val quitItem = new MenuItem("Quit")
    quitItem.connect(new MenuItem.Activate() {
      def onActivate(menuItem: MenuItem) {
        //Gtk.mainQuit() // TODO replace with window.destroy()
      }
    })
    quitMenu.append(quitItem)
    pomodoroItem.setSubmenu(quitMenu)

  }

  def buildHelpMenu {
    // Help Menu
    val helpItem = new MenuItem("Help")
    menuBar.append(helpItem)

    // submenu items
    val helpMenu = new Menu()
    val aboutItem = new MenuItem("About")
    aboutItem.connect(new MenuItem.Activate() {
      def onActivate(menuItem: MenuItem) {
        showAboutDialog

      }
    })
    helpMenu.append(aboutItem)
    helpItem.setSubmenu(helpMenu)

  }

  private def showAboutDialog {
    val logo = new Pixbuf("src/main/resources/palatina-160x70.png")
    val about = new AboutDialog()
    about.setProgramName("TomatoJuice");
    //about.setIcon(Icons.PalatinaIcon)
    about.setVersion("0.0.1");
    about.setCopyright("\u00A9 2014 Cathal Mc Ginley");
    about.setComments("TomatoJuice is a Pomodoro time tracker")
    about.setLogo(logo); // TODO stack of books logo
    about.setLicense(LicenseGPLv3)
    //about.setLicenseType(License.GPL_3_0) // TODO - also note setLicenseType is in JavaGnome 4.1.2
    about.setWrapLicense(true)
    about.setWebsite("http://palatin.as")
    about.setWebsiteLabel("http://palatin.as")
    about.setPosition(WindowPosition.CENTER);
    about.run();
    about.hide();

  }

  val LicenseGPLv3 =
    "TomatoJuice is free software; you can redistribute it and/or modify it" +
      " under the terms of the GNU General Public License as published by" +
      " the Free Software Foundation; either version 3 of the License, or" +
      " (at your option) any later version.\n" +
      "\n" +
      "TomatoJuice is distributed in the hope that it will be useful, but" +
      " WITHOUT ANY WARRANTY; without even the implied warranty of" +
      " MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU" +
      " General Public License for more details.\n" +
      "\n" +
      "You should have received a copy of the GNU General Public License" +
      " along with TomatoJuice; see the file COPYING. If not, write to the" +
      " Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor," +
      " Boston, MA 02110-1301 USA."

}
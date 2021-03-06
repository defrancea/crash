/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 *
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.crsh;

import org.crsh.plugin.*;
import org.crsh.shell.impl.command.CRaSH;
import org.crsh.shell.impl.command.CRaSHSession;
import org.crsh.vfs.FS;
import org.crsh.vfs.Path;
import org.crsh.vfs.spi.ram.RAMDriver;

import java.util.HashMap;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 */
public class TestPluginLifeCycle extends PluginLifeCycle {

  /** . */
  private final PluginContext context;

  /** . */
  private CRaSH crash;

  /** . */
  private HashMap<String, Object> attributes;

  /** . */
  private final RAMDriver commands;

  public TestPluginLifeCycle() throws Exception {
    this(Thread.currentThread().getContextClassLoader());
  }

  public TestPluginLifeCycle(ClassLoader classLoader) throws Exception {
    this(new ServiceLoaderDiscovery(classLoader), classLoader);
  }

  public TestPluginLifeCycle(CRaSHPlugin... plugins) throws Exception {
    this(new SimplePluginDiscovery(plugins), Thread.currentThread().getContextClassLoader());
  }

  private TestPluginLifeCycle(PluginDiscovery discovery, ClassLoader classLoader) throws Exception {
    this.attributes = new HashMap<String, Object>();
    this.commands = new RAMDriver();
    this.context = new PluginContext(
      discovery,
      attributes,
      new FS().mount(classLoader,Path.get("/crash/commands/")).mount(commands, "/"),
      new FS().mount(classLoader,Path.get("/crash/")),
      classLoader);
    this.crash = new CRaSH(context);
  }

  public void setCommand(String name, String command) {
    if (name.contains("/")) {
      throw new IllegalArgumentException("Command name must not contain /");
    }
    if (name.contains(".")) {
      throw new IllegalArgumentException("Command name must not contain .");
    }
    commands.add(Path.get("/" + name + ".groovy"), command);
    context.refresh();
  }

  public Object getAttribute(String name) {
    return attributes.get(name);
  }

  public void setAttribute(String name, Object value) {
    if (value != null) {
      attributes.put(name, value);
    } else {
      attributes.remove(name);
    }
  }

  public <T> void setProperty(PropertyDescriptor<T> desc, T value) throws NullPointerException {
    context.setProperty(desc, value);
  }

  public void start() {
    context.refresh();
    start(context);
  }

  public CRaSHSession createShell() {
    return crash.createSession(null);
  }
}



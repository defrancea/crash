/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
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
package org.crsh.jcr;

import org.crsh.util.Safe;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ImportUUIDBehavior;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SinkCommand extends SCPCommand implements Runnable {

  /** . */
  private boolean recursive;

  public SinkCommand(String target, boolean recursive) {
    super(target);

    //
    this.recursive = recursive;
  }

  @Override
  protected void execute(final Session session, final String path) throws Exception {

    // FS that will import
    FileSystem fs = new FileSystem() {
      public void startDirectory(String directoryName) throws IOException {
      }
      public void file(String fileName, int length, InputStream data) throws IOException {
        try {
          session.getWorkspace().importXML(path, data, ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);
        }
        catch (RepositoryException e) {
          Safe.rethrow(IOException.class, e);
        }
      }
      public void endDirectory(String directoryName) throws IOException {
      }
    };

    //
    FileSystemAction.read(this, fs);
  }
}

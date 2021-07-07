/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.showcase.service;

import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.showcase.domain.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class DocumentService {

    public TreeNode<Document> createDocuments() {
        TreeNode<Document> root = new DefaultTreeNode(new Document("Files", "-", "Folder"), null);

        TreeNode applications = new DefaultTreeNode(new Document("Applications", "100kb", "Folder"), root);
        TreeNode cloud = new DefaultTreeNode(new Document("Cloud", "20kb", "Folder"), root);
        TreeNode desktop = new DefaultTreeNode(new Document("Desktop", "150kb", "Folder"), root);
        TreeNode documents = new DefaultTreeNode(new Document("Documents", "75kb", "Folder"), root);
        TreeNode downloads = new DefaultTreeNode(new Document("Downloads", "25kb", "Folder"), root);
        TreeNode main = new DefaultTreeNode(new Document("Main", "50kb", "Folder"), root);
        TreeNode other = new DefaultTreeNode(new Document("Other", "5kb", "Folder"), root);
        TreeNode pictures = new DefaultTreeNode(new Document("Pictures", "150kb", "Folder"), root);
        TreeNode videos = new DefaultTreeNode(new Document("Videos", "1500kb", "Folder"), root);

        //Applications
        TreeNode primeface = new DefaultTreeNode(new Document("Primefaces", "25kb", "Folder"), applications);
        TreeNode primefacesapp = new DefaultTreeNode("app", new Document("primefaces.app", "10kb", "Application"), primeface);
        TreeNode nativeapp = new DefaultTreeNode("app", new Document("native.app", "10kb", "Application"), primeface);
        TreeNode mobileapp = new DefaultTreeNode("app", new Document("mobile.app", "5kb", "Application"), primeface);
        TreeNode editorapp = new DefaultTreeNode("app", new Document("editor.app", "25kb", "Application"), applications);
        TreeNode settingsapp = new DefaultTreeNode("app", new Document("settings.app", "50kb", "Application"), applications);

        //Cloud
        TreeNode backup1 = new DefaultTreeNode("document", new Document("backup-1.zip", "10kb", "Zip"), cloud);
        TreeNode backup2 = new DefaultTreeNode("document", new Document("backup-2.zip", "10kb", "Zip"), cloud);

        //Desktop
        TreeNode note1 = new DefaultTreeNode("document", new Document("note-meeting.txt", "50kb", "Text"), desktop);
        TreeNode note2 = new DefaultTreeNode("document", new Document("note-todo.txt", "100kb", "Text"), desktop);

        //Documents
        TreeNode work = new DefaultTreeNode(new Document("Work", "55kb", "Folder"), documents);
        TreeNode expenses = new DefaultTreeNode("document", new Document("Expenses.doc", "30kb", "Document"), work);
        TreeNode resume = new DefaultTreeNode("document", new Document("Resume.doc", "25kb", "Resume"), work);
        TreeNode home = new DefaultTreeNode(new Document("Home", "20kb", "Folder"), documents);
        TreeNode invoices = new DefaultTreeNode("excel", new Document("Invoice.xsl", "20kb", "Excel"), home);

        //Downloads
        TreeNode spanish = new DefaultTreeNode(new Document("Spanish", "10kb", "Folder"), downloads);
        TreeNode tutorial1 = new DefaultTreeNode("document", new Document("tutorial-a1.txt", "5kb", "Text"), spanish);
        TreeNode tutorial2 = new DefaultTreeNode("document", new Document("tutorial-a2.txt", "5kb", "Text"), spanish);
        TreeNode travel = new DefaultTreeNode(new Document("Travel", "15kb", "Folder"), downloads);
        TreeNode hotelpdf = new DefaultTreeNode("travel", new Document("Hotel.pdf", "10kb", "PDF"), travel);
        TreeNode flightpdf = new DefaultTreeNode("travel", new Document("Flight.pdf", "5kb", "PDF"), travel);

        //Main
        TreeNode bin = new DefaultTreeNode("document", new Document("bin", "50kb", "Link"), main);
        TreeNode etc = new DefaultTreeNode("document", new Document("etc", "100kb", "Link"), main);
        TreeNode var = new DefaultTreeNode("document", new Document("var", "100kb", "Link"), main);

        //Other
        TreeNode todotxt = new DefaultTreeNode("document", new Document("todo.txt", "3kb", "Text"), other);
        TreeNode logopng = new DefaultTreeNode("picture", new Document("logo.png", "2kb", "Picture"), other);

        //Pictures
        TreeNode barcelona = new DefaultTreeNode("picture", new Document("barcelona.jpg", "90kb", "Picture"), pictures);
        TreeNode primeng = new DefaultTreeNode("picture", new Document("primefaces.png", "30kb", "Picture"), pictures);
        TreeNode prime = new DefaultTreeNode("picture", new Document("prime.jpg", "30kb", "Picture"), pictures);

        //Videos
        TreeNode primefacesmkv = new DefaultTreeNode("video", new Document("primefaces.mkv", "1000kb", "Video"), videos);
        TreeNode introavi = new DefaultTreeNode("video", new Document("intro.avi", "500kb", "Video"), videos);

        return root;
    }

    public TreeNode<Document> createCheckboxDocuments() {
        TreeNode<Document> root = new CheckboxTreeNode(new Document("Files", "-", "Folder"), null);

        TreeNode applications = new CheckboxTreeNode(new Document("Applications", "100kb", "Folder"), root);
        TreeNode cloud = new CheckboxTreeNode(new Document("Cloud", "20kb", "Folder"), root);
        TreeNode desktop = new CheckboxTreeNode(new Document("Desktop", "150kb", "Folder"), root);
        TreeNode documents = new CheckboxTreeNode(new Document("Documents", "75kb", "Folder"), root);
        TreeNode downloads = new CheckboxTreeNode(new Document("Downloads", "25kb", "Folder"), root);
        TreeNode main = new CheckboxTreeNode(new Document("Main", "50kb", "Folder"), root);
        TreeNode other = new CheckboxTreeNode(new Document("Other", "5kb", "Folder"), root);
        TreeNode pictures = new CheckboxTreeNode(new Document("Pictures", "150kb", "Folder"), root);
        TreeNode videos = new CheckboxTreeNode(new Document("Videos", "1500kb", "Folder"), root);

        //Applications
        TreeNode primeface = new CheckboxTreeNode(new Document("Primefaces", "25kb", "Folder"), applications);
        TreeNode primefacesapp = new CheckboxTreeNode("app", new Document("primefaces.app", "10kb", "Application"), primeface);
        TreeNode nativeapp = new CheckboxTreeNode("app", new Document("native.app", "10kb", "Application"), primeface);
        TreeNode mobileapp = new CheckboxTreeNode("app", new Document("mobile.app", "5kb", "Application"), primeface);
        TreeNode editorapp = new CheckboxTreeNode("app", new Document("editor.app", "25kb", "Application"), applications);
        TreeNode settingsapp = new CheckboxTreeNode("app", new Document("settings.app", "50kb", "Application"), applications);

        //Cloud
        TreeNode backup1 = new CheckboxTreeNode("document", new Document("backup-1.zip", "10kb", "Zip"), cloud);
        TreeNode backup2 = new CheckboxTreeNode("document", new Document("backup-2.zip", "10kb", "Zip"), cloud);

        //Desktop
        TreeNode note1 = new CheckboxTreeNode("document", new Document("note-meeting.txt", "50kb", "Text"), desktop);
        TreeNode note2 = new CheckboxTreeNode("document", new Document("note-todo.txt", "100kb", "Text"), desktop);

        //Documents
        TreeNode work = new CheckboxTreeNode(new Document("Work", "55kb", "Folder"), documents);
        TreeNode expenses = new CheckboxTreeNode("document", new Document("Expenses.doc", "30kb", "Document"), work);
        TreeNode resume = new CheckboxTreeNode("document", new Document("Resume.doc", "25kb", "Resume"), work);
        TreeNode home = new CheckboxTreeNode(new Document("Home", "20kb", "Folder"), documents);
        TreeNode invoices = new CheckboxTreeNode("excel", new Document("Invoice.xsl", "20kb", "Excel"), home);

        //Downloads
        TreeNode spanish = new CheckboxTreeNode(new Document("Spanish", "10kb", "Folder"), downloads);
        TreeNode tutorial1 = new CheckboxTreeNode("document", new Document("tutorial-a1.txt", "5kb", "Text"), spanish);
        TreeNode tutorial2 = new CheckboxTreeNode("document", new Document("tutorial-a2.txt", "5kb", "Text"), spanish);
        TreeNode travel = new CheckboxTreeNode(new Document("Travel", "15kb", "Folder"), downloads);
        TreeNode hotelpdf = new CheckboxTreeNode("travel", new Document("Hotel.pdf", "10kb", "PDF"), travel);
        TreeNode flightpdf = new CheckboxTreeNode("travel", new Document("Flight.pdf", "5kb", "PDF"), travel);

        //Main
        TreeNode bin = new CheckboxTreeNode("document", new Document("bin", "50kb", "Link"), main);
        TreeNode etc = new CheckboxTreeNode("document", new Document("etc", "100kb", "Link"), main);
        TreeNode var = new CheckboxTreeNode("document", new Document("var", "100kb", "Link"), main);

        //Other
        TreeNode todotxt = new CheckboxTreeNode("document", new Document("todo.txt", "3kb", "Text"), other);
        TreeNode logopng = new CheckboxTreeNode("picture", new Document("logo.png", "2kb", "Picture"), other);

        //Pictures
        TreeNode barcelona = new CheckboxTreeNode("picture", new Document("barcelona.jpg", "90kb", "Picture"), pictures);
        TreeNode primeng = new CheckboxTreeNode("picture", new Document("primefaces.png", "30kb", "Picture"), pictures);
        TreeNode prime = new CheckboxTreeNode("picture", new Document("prime.jpg", "30kb", "Picture"), pictures);

        //Videos
        TreeNode primefacesmkv = new CheckboxTreeNode("video", new Document("primefaces.mkv", "1000kb", "Video"), videos);
        TreeNode introavi = new CheckboxTreeNode("video", new Document("intro.avi", "500kb", "Video"), videos);

        return root;
    }
}

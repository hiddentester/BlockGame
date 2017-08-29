/*******************************
 * This class renders the game *
 *******************************/

package com.hiddentester.blockGame.io;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import com.hiddentester.math.Vector2D;
import com.hiddentester.blockGame.blocks.Block;
import com.hiddentester.blockGame.blocks.Stone;
import com.hiddentester.blockGame.core.Game;
import com.hiddentester.blockGame.entities.Player;
import com.hiddentester.blockGame.core.Chunk;

public class Drawing extends JComponent {
	private static final int FIELD_OF_VIEW = 32;		//This defines how many blocks fit across the window
	private int blockSize;								//This is the size of a block in pixels
	private ImageIcon[] textures;
	private Game game;

	//Constructor
	public Drawing (Game game) {
		super();
		this.game = game;
		updateScale();
	}

	//Load textures
	public static ImageIcon[] loadTextures (String folderName) {
		File folder = new File("./" + folderName + "/");
		File[] files = folder.listFiles();
		ImageIcon[] textures = new ImageIcon[files.length];

		for (int i = 0; i < textures.length; i++) {
			textures[i] = new ImageIcon(folderName + "/" + files[i].getName());
		}

		return textures;
	}

	//Converts mouse click coordinates to block coordinates
	public Vector2D getClickPos (Vector2D clickPos) {
		Vector2D centre = new Vector2D(getWidth() / 2, getHeight() / 2);
		Vector2D offset = Vector2D.sub(clickPos, centre);
		offset = Vector2D.scale(offset, 1.0 / blockSize);
		offset.setMagY(-offset.getMagY());

		Vector2D playerPos = game.getPlayer().getAbsPos();
		return Vector2D.add(playerPos, offset);
	}

	//Calculate scaling of the block
	private void updateScale () {
		blockSize = this.getWidth() / FIELD_OF_VIEW;
	}

	//Paint method
	@Override
	public void paint (Graphics g) {
		updateScale();

		//Fill background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		//Offset graphics
		g.translate(this.getWidth() / 2, this.getHeight() / 2);

		Player player = game.getPlayer();
		Chunk[][] loadedChunks = game.getLoadedChunks();

		//Loop through each chunk
		for (int chunk_i = 0; chunk_i < loadedChunks.length; chunk_i++) {
			for (int chunk_j = 0; chunk_j < loadedChunks[chunk_i].length; chunk_j++) {
				Chunk curChunk = loadedChunks[chunk_i][chunk_j];

				Block[][] blocks = curChunk.getBlocks();

				//Loop through each block
				for (int i = 0; i < blocks.length; i++) {
					for (int j = 0; j < blocks[i].length; j++) {
						//Get and scale position of block relative to player
						Vector2D relPos = Vector2D.sub(Vector2D.scale(curChunk.getPos(), Chunk.SIZE), player.getAbsPos());
						relPos.setMagX(relPos.getMagX() + i);
						relPos.setMagY(relPos.getMagY() + j);
						relPos = Vector2D.scale(relPos, blockSize);

						//Select colour
						if (blocks[i][j] instanceof Stone) {
							g.setColor(Color.GRAY);
						} else {
							g.setColor(Color.WHITE);
						}

						//Draw block
						g.fillRect((int) (relPos.getMagX()), -(int) (relPos.getMagY()), blockSize, -blockSize);
					}
				}
			}
		}

		//Draw player
		Vector2D relDim = Vector2D.scale(player.getDimensions(), blockSize);

		g.setColor(Color.BLUE);
		g.fillRect((int) (-0.5 * relDim.getMagX()), 0, (int) (relDim.getMagX()), -(int) (relDim.getMagY()));

		//Reset graphics translation
		g.translate(-this.getWidth() / 2, -this.getHeight() / 2);

		//Mark centre
		g.setColor(Color.GREEN);
		g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
		g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
	}
}
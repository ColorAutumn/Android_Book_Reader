package com.example.readbookapplication.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.readbookapplication.pojo.Chapter;
import com.example.readbookapplication.pojo.Novel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "novel_database.db";
    private static final int DATABASE_VERSION = 1;

    // 表名和字段名
    private static final String TABLE_NOVELS = "novels";
    private static final String COLUMN_NOVEL_ID = "id";
    private static final String COLUMN_NOVEL_URL_LIST = "novel_url_list";
    private static final String COLUMN_NOVEL_TITLE = "novel_title";
    private static final String COLUMN_NOVEL_AUTHOR = "novel_author";
    private static final String COLUMN_NOVEL_IMAGE_URL = "novel_image_url";
    private static final String COLUMN_NOVEL_DESCRIPTION = "novel_description";

    // 章节内容表名和字段名
    private static final String TABLE_CHAPTERS = "chapters";
    private static final String COLUMN_CHAPTER_ID = "id";
    private static final String COLUMN_CHAPTER_TITLE = "chapter_title";
    private static final String COLUMN_CHAPTER_NOVEL_NAME = "chapter_novel_name";
    private static final String COLUMN_CHAPTER_CONTENT = "chapter_content";
    private static final String COLUMN_CHAPTER_PB_PREV = "chapter_pb_prev";
    private static final String COLUMN_CHAPTER_PB_NEXT = "chapter_pb_next";
    private static final String COLUMN_CHAPTER_URL = "chapter_url";


    // SQL 语句
    private static final String CREATE_TABLE_NOVELS = "CREATE TABLE " + TABLE_NOVELS + " ("
            + COLUMN_NOVEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NOVEL_URL_LIST + " TEXT, "
            + COLUMN_NOVEL_TITLE + " TEXT, "
            + COLUMN_NOVEL_AUTHOR + " TEXT, "
            + COLUMN_NOVEL_IMAGE_URL + " TEXT, "
            + COLUMN_NOVEL_DESCRIPTION + " TEXT "
            + ")";

    // SQL 语句
    private static final String CREATE_TABLE_CHAPTERS = "CREATE TABLE " + TABLE_CHAPTERS + " ("
            + COLUMN_CHAPTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CHAPTER_TITLE + " TEXT, "
            + COLUMN_CHAPTER_NOVEL_NAME + " TEXT, "
            + COLUMN_CHAPTER_CONTENT + " TEXT, "
            + COLUMN_CHAPTER_PB_PREV + " TEXT, "
            + COLUMN_CHAPTER_PB_NEXT + " TEXT, "
            + COLUMN_CHAPTER_URL + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOVELS);
        db.execSQL(CREATE_TABLE_CHAPTERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 在数据库升级时的操作，可以根据需要进行处理
    }


    // 加入书架
    public boolean addToBookshelf(Novel novel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOVEL_URL_LIST, novel.getUrl_list());
        values.put(COLUMN_NOVEL_TITLE, novel.getTitle());
        values.put(COLUMN_NOVEL_AUTHOR, novel.getAuthor());
        values.put(COLUMN_NOVEL_IMAGE_URL, novel.getImg_url());
        values.put(COLUMN_NOVEL_DESCRIPTION, novel.getDescription());

        long id = db.insert(TABLE_NOVELS, null, values);
        db.close();
        return id > 0;
    }

    // 移出书架
    public boolean removeFromShelf(String novelId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_NOVEL_URL_LIST + " = ?";
        String[] whereArgs = {novelId};

        int rowsDeleted = db.delete(TABLE_NOVELS, whereClause, whereArgs);

        db.close();

        return rowsDeleted > 0;
    }

    // 根据ID查询书籍
    @SuppressLint("Range")
    public Novel getNovelById(String novelId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_NOVEL_URL_LIST + " = ?";
        String[] selectionArgs = {novelId};

        Cursor cursor = db.query(
                TABLE_NOVELS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Novel novel = null;

        if (cursor != null && cursor.moveToFirst()) {
            novel = new Novel();
            novel.setUrl_list(cursor.getString(cursor.getColumnIndex(COLUMN_NOVEL_URL_LIST)));
            novel.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOVEL_TITLE)));
            novel.setAuthor(cursor.getString(cursor.getColumnIndex(COLUMN_NOVEL_AUTHOR)));
            novel.setImg_url(cursor.getString(cursor.getColumnIndex(COLUMN_NOVEL_IMAGE_URL)));
            novel.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_NOVEL_DESCRIPTION)));
            cursor.close();
        }

        db.close();
        return novel;
    }


    // 查询所有小说
    @SuppressLint("Range")
    public List<Novel> getAllNovels() {
        List<Novel> novels = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NOVELS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Novel novel = new Novel();
                novel.setUrl_list(cursor.getString(cursor.getColumnIndex(COLUMN_NOVEL_URL_LIST)));
                novel.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOVEL_TITLE)));
                novel.setAuthor(cursor.getString(cursor.getColumnIndex(COLUMN_NOVEL_AUTHOR)));
                novel.setImg_url(cursor.getString(cursor.getColumnIndex(COLUMN_NOVEL_IMAGE_URL)));
                novel.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_NOVEL_DESCRIPTION)));

                novels.add(novel);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return novels;
    }


    public boolean addChapter(Chapter chapter) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHAPTER_TITLE, chapter.getTitle());
        values.put(COLUMN_CHAPTER_NOVEL_NAME, chapter.getNovelName());
        values.put(COLUMN_CHAPTER_CONTENT, chapter.getContent());
        values.put(COLUMN_CHAPTER_PB_PREV, chapter.getPbPrev());
        values.put(COLUMN_CHAPTER_PB_NEXT, chapter.getPbNext());
        values.put(COLUMN_CHAPTER_URL, chapter.getUrl());

        long id = db.insertWithOnConflict(TABLE_CHAPTERS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return id > 0;
    }

    // 检查章节是否在数据库中
    public boolean isChapterInDatabase(String chapterUrl) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_CHAPTER_URL + " = ?";
        String[] selectionArgs = {chapterUrl};

        Cursor cursor = db.query(
                TABLE_CHAPTERS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return exists;
    }

    // 从数据库中获取章节
    @SuppressLint("Range")
    public Chapter getNovelChapter(String chapterUrl) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_CHAPTER_URL + " = ?";
        String[] selectionArgs = {chapterUrl};

        Cursor cursor = db.query(
                TABLE_CHAPTERS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Chapter chapter = null;

        if (cursor != null && cursor.moveToFirst()) {
            chapter = new Chapter();
            chapter.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_CHAPTER_URL)));
            chapter.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_CHAPTER_TITLE)));
            chapter.setNovelName(cursor.getString(cursor.getColumnIndex(COLUMN_CHAPTER_NOVEL_NAME)));
            chapter.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CHAPTER_CONTENT)));
            chapter.setPbPrev(cursor.getString(cursor.getColumnIndex(COLUMN_CHAPTER_PB_PREV)));
            chapter.setPbNext(cursor.getString(cursor.getColumnIndex(COLUMN_CHAPTER_PB_NEXT)));
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return chapter;
    }

    // 从数据库中删除章节
    public boolean removeChapter(String chapterUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_CHAPTER_URL + " = ?";
        String[] whereArgs = {chapterUrl};
        int rowsDeleted = db.delete(TABLE_CHAPTERS, whereClause, whereArgs);
        db.close();
        return rowsDeleted > 0;
    }
    // 其他数据库操作方法...
}

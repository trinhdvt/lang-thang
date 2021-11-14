import React, { useRef, useState } from 'react'
import { EditorState, RichUtils } from "draft-js";
import { Editor } from "react-draft-wysiwyg";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import { stateToHTML } from "draft-js-export-html";
import { stateFromHTML } from 'draft-js-import-html';
import axios from 'axios';
import { useEffect } from 'react';
import { useHistory, useParams } from 'react-router';
import ReactHtmlParser from 'react-html-parser'
import newpostApis from './enum/newpost-apis';
import { errorNotification, successNotification } from '../../utils/notification/ToastNotification';
import image from '../../../asset/editor-imgs/image.svg'
import { isImgFormat, isImgSize } from '../../utils/validation/Validation';
import { useSelector } from 'react-redux';


function NewPosts() {
  const params = useParams()
  const history = useHistory()
  const initialState = {
    title: '',
    postThumbnail: '',
    categories: [],
  }
  const [content, setContent] = useState(EditorState.createEmpty())
  const [data, setData] = useState(initialState)
  const [post, setPost] = useState({})
  const [category, setCategory] = useState([])
  const unmounted = useRef(false)
  const auth = useSelector(state => state.auth)

  useEffect(() => {
    const getCate = async () => {
      const res = await axios.get(newpostApis.getCategories)
      if (!unmounted.current) {
        setCategory(res.data)
      }
    }
    getCate()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  useEffect(() => {
    if (params.slug) {
      const getPost = async () => {
        const res = await axios.get(newpostApis.loadPost(params.slug))
        var _thumbnail = ReactHtmlParser(res.data.postThumbnail)[0]
        var text = stateFromHTML(ReactHtmlParser(res.data.content)[0])
        var _content = EditorState.createWithContent(text)
        if (!unmounted.current) {
          setData({
            title: res.data.title,
            postThumbnail: _thumbnail,
            categories: [...data.categories, res.data.categories[0].categoryId]
          })
          setPost(res.data)
          setContent(_content)
        }
      }
      getPost()
    }
    else {
      if (!unmounted.current) {
        setData(initialState)
        setContent(EditorState.createEmpty())
      }
    }
  }, [params.slug])

  useEffect(() => {
    return () => {
      unmounted.current = true
    }
  }, [])
 

  const handleChange = (e) => {
    const target = e.target
    const { name, value } = target
    setData({
      ...data,
      [name]: value
    })
  }

  const handleChangeCate = e => {
    // var id = e.target.selectedIndex
    if (e.target.value !== 'nothing') {
      var id = e.target.value
      var cate = []
      cate.push(parseInt(id))
      setData({ ...data, categories: cate })
    }
  }

  const handleChangeAvatar = async (e) => {
    e.preventDefault()
    try {
      const file = e.target.files[0]
      if (!file) {
        return
      }
      // if (file.type !== 'image/jpeg' && file.type !== 'image/png') {
      //   return errorNotification("Sai định dạng")
      // }
      if(!isImgFormat(file)) return errorNotification("Sai định dạng")

      // if(file.size >= 1024*1024*2){
      //   return errorNotification("Dung lượng ảnh phải nhỏ hơn 2MB")
      // } // 2mb

      if(!isImgSize(file)) return errorNotification("Dung lượng ảnh phải nhỏ hơn 2MB")


      var formImage = new FormData()
      formImage.append('upload', file)

      const res = await axios.post(newpostApis.uploadImg, formImage)

      if (res) {
        setData({ ...data, postThumbnail: res.data.url })
      }
    } catch (error) {
      if(error.response.status === 413) {
        errorNotification("Dung lượng ảnh phải nhỏ hơn 2MB")
      }
      else errorNotification("Đã xảy ra lỗi")
    }
  }

  const onEditorChange = (editorState) => {
    setContent(editorState)
  }

  function uploadImageCallBack(file) {
    return new Promise(async (resolve, reject) => {
      if(!isImgFormat(file)) return reject(errorNotification("Sai định dạng"))
      
      if(!isImgSize(file)) return reject(errorNotification("Dung lượng ảnh phải nhỏ hơn 2MB"))
      
      const data = new FormData();
      data.append('upload', file);
      try {
        const res = await axios.post(newpostApis.uploadImg, data)
        resolve({
          data: {
            link: res.data.url
          }
        });
      } catch (e) {
        if(e.response.status === 413) {
          reject(errorNotification("Dung lượng ảnh phải nhỏ hơn 2MB"))
        }
        reject(e)
      }
    })
  }

  const checkError = () => {
    if (!data.title) {
      return errorNotification('Hãy nhập tiêu đề bài viết')
    }
    
    if(data.title.length > 200) {
      return errorNotification('Hãy nhập tiêu đề có độ dài ít hơn 200 ký tự')
    }

    if (!data.postThumbnail) {
      return errorNotification('Hãy thêm ảnh bìa bài viết')
    }

    if (!content.getCurrentContent().getPlainText().trim()) {
      return errorNotification('Hãy nhập nội dung bài viết')
    }

    if (data.categories.length === 0) {
      return errorNotification('Hãy chọn thể loại bài viết')
    }
    return true
  }

  const handleSubmitPost = (e) => {
    e.preventDefault()
    const _checkError = checkError()

    if (_checkError === true) {
      console.log("submit")
      var formPost = new FormData()
      formPost.append("title", data.title)
      formPost.append("content", stateToHTML(content.getCurrentContent()))
      formPost.append("postThumbnail", data.postThumbnail)
      formPost.append("categories", data.categories)

      const postPost = async () => {
        try {
          const res = await axios.post(newpostApis.savePost, formPost)
          if (res) {
            successNotification('Đăng bài thành công 🎉')
            history.push(`/posts/${res.data.slug}`)
          }
        } catch (error) {
          errorNotification("Không thể đăng bài viết")
        }
      }
      postPost()
    }


  }

  const handleEditPost = (e) => {
    e.preventDefault()
    const _checkError = checkError()
    if (_checkError === true) {
      const editPost = async () => {
        var formPost = new FormData()
        formPost.append("title", data.title)
        formPost.append("content", stateToHTML(content.getCurrentContent()))

        formPost.append("postThumbnail", data.postThumbnail)
        formPost.append("categories", data.categories)
        try {
          const res = await axios.put(newpostApis.updatePost(post.postId), formPost)
          if (res) {
            successNotification('Sửa bài viết thành công 🎉')
            history.push(`/posts/${res.data.slug}`)
          }
        } catch (error) {
          errorNotification("Không thể chỉnh sửa bài viết")
        }
      }
      editPost()
    }
  }

  const handleSubmitDraft = (e) => {
    e.preventDefault()
    const _checkError = checkError()
    if (_checkError === true) {
      var formDraft = new FormData()
      formDraft.append("title", data.title)
      formDraft.append("content", stateToHTML(content.getCurrentContent()))
      formDraft.append("postThumbnail", data.postThumbnail)
      formDraft.append("categories", data.categories)

      const postDraft = async () => {
        try {
          const res = await axios.post(newpostApis.saveDraft, formDraft)
          if (res) {
            successNotification('Đã lưu lại bản nháp ✔')
            history.push(`/profile/${auth.user.accountId}`)
          }
        } catch (error) {
          errorNotification("Không thể lưu bản nháp")
        }
      }
      postDraft()
    }
  }
  //Chuyen bai viet thanh ban nhap hoac sua ban nhap
  const handleEditDraft = async (e) => {
    e.preventDefault()
    var formDraft = new FormData()
    formDraft.append("title", data.title)
    formDraft.append("content", stateToHTML(content.getCurrentContent()))

    formDraft.append("postThumbnail", data.postThumbnail)
    formDraft.append("categories", data.categories)

    try {
      const res = await axios.put(newpostApis.updateDraft(post.postId), formDraft)
      if (res) {
        successNotification('Đã lưu thành bản nháp')
        history.push(`/profile/${auth.user.accountId}`)
      }
    } catch (error) {
      errorNotification("Không thể lưu bản nháp")
    }
  }

  return (
    <main className="main__home">
      <div className="container">
        <div className="row">
          <div className="col-lg-12">
            <input className="newpost__input mt-30"
              type="text"
              placeholder="Tựa đề hay gây ấn tượng cho người đọc"
              onChange={handleChange} value={data.title} name="title"
            />

            <label className="newpost__thumnail-btn">
              <i className="fa fa-image"></i>
              <input type="file" style={{ display: 'none' }} name="postThumbnail"
                onChange={(e) => handleChangeAvatar(e)}
              />

              {data.postThumbnail ? data.postThumbnail : 'Chọn ảnh bìa cho bài viết của bạn'}
            </label>

            <Editor
              editorState={content}
              onEditorStateChange={onEditorChange}
              handleKeyCommand={(command) => {
                let newState = RichUtils.handleKeyCommand(content, command)
                if (newState) {
                  onEditorChange(newState)
                  return "handled"
                }
                return "not-handled"
              }}
              toolbarClassName="toolbarClassName"
              wrapperClassName="wrapperClassName"
              editorClassName="editorClassName"
              toolbar={{
                options: ['inline', 'link', 'list', 'image', 'history'],
                inline: {
                   options: ['bold', 'italic', 'underline', 'strikethrough'],
                   },
                list: { options: ['ordered']},
                image: {
                  icon: image,
                  className: "new-post__eidtor--img-custom",
                  previewImage: true,
                  alignmentEnabled: false,
                  uploadCallback: uploadImageCallBack, alt: { present: true, mandatory: false },
                  inputAccept: 'image/jpeg,image/jpg,image/png',
                  defaultSize: {
                    height: '100%',
                    width: '100%',
                  },
                },
              }}
            />
            <p className="mt-10">Bài viết của bạn thuộc thể loại: </p>

            <select className="mt-10 mb-15 newpost__option" onChange={handleChangeCate} id="categories" value={data.categories[0]} defaultValue="Chọn thể loại">
              <option value="nothing">Chọn thể loại</option>
              {
                category.map((item) => {
                  return (
                    <option key={item.categoryId} id={item.categoryId} value={item.categoryId}>{item.categoryName}</option>
                  )
                })
              }

            </select>

            <div className="d-flex justify-content-end mb-50">
              {params.slug ?
                <>
                  <form className="mr-10" onSubmit={handleEditDraft}>
                    <button className="button button-primary-no-hover mb-15" type="submit">Lưu nháp</button>
                  </form>

                  <form onSubmit={handleEditPost}>
                    <button className="button button-primary mb-15" type="submit">Đăng bài</button>
                  </form>
                </>
                :
                <>
                  <form className="mr-10" onSubmit={handleSubmitDraft}>
                    <button className="button button-primary-no-hover mb-15" type="submit">Lưu nháp</button>
                  </form>

                  <form onSubmit={handleSubmitPost} >
                    <button className="button button-primary mb-15" type="submit">Đăng bài</button>
                  </form>
                </>
              }
            </div>
          </div>
        </div>
      </div>
    </main>
  )
}

export default NewPosts;


import {afterEach,describe,it,expect,vi} from 'vitest'
import {cleanup,fireEvent,render,screen,waitFor} from '@testing-library/react'
import {Auth} from './App'
import {api,csrf} from './api/client'
vi.mock('./api/client',()=>({api:{post:vi.fn()},csrf:vi.fn(),errorMessage:e=>e?.response?.data?.message||'Something went wrong. Please try again.'}))
describe('authentication form',()=>{
 afterEach(()=>{cleanup();vi.clearAllMocks()})
 it('rejects a too-short registration password before sending the request',()=>{render(<Auth onDone={()=>{}}/>);fireEvent.click(screen.getByRole('button',{name:'Create an account'}));fireEvent.change(screen.getByLabelText('Your name'),{target:{value:'Sam Lee'}});fireEvent.change(screen.getByLabelText('Work email'),{target:{value:'sam@example.com'}});fireEvent.change(screen.getByLabelText('Password'),{target:{value:'short'}});fireEvent.submit(screen.getByLabelText('Password').closest('form'));expect(screen.getByRole('alert')).toHaveTextContent('at least 10 characters');expect(api.post).not.toHaveBeenCalled()})
 it('shows a safe API error when sign in fails',async()=>{csrf.mockResolvedValueOnce();api.post.mockRejectedValueOnce({response:{data:{message:'Email or password is incorrect.'}}});render(<Auth onDone={()=>{}}/>);fireEvent.change(screen.getByLabelText('Work email'),{target:{value:'sam@example.com'}});fireEvent.change(screen.getByLabelText('Password'),{target:{value:'valid-password'}});fireEvent.click(screen.getByRole('button',{name:'Sign in'}));expect(await screen.findByRole('alert')).toHaveTextContent('Email or password is incorrect.');await waitFor(()=>expect(csrf).toHaveBeenCalled())})
})
